import os
import pygame
import neat
import random
import pickle
import json
import warnings

warnings.filterwarnings("ignore", message=".*initial_connection.*")

# === CONFIGURATION ===
CONTINUE_FROM_BEST = False
GENERATIONS_TO_TRAIN = 50
DRAW_GAME = True
FPS = 30
MAX_FRAMES_PER_GEN = 4000
STAGNATION_LIMIT = 900

# === Modes ===
MODE_AI_ONLY = 1
MODE_HUMAN_VS_AI = 2
CURRENT_MODE = MODE_AI_ONLY
RESTART_REQUESTED = False

# === Game Constants ===
WIN_WIDTH = 288
WIN_HEIGHT = 512
FLOOR_Y = 512 - 112
PIPE_GAP = 140
PIPE_VEL = 3
PIPE_SPAWN_DISTANCE = 180
BIRD_START_X = 50
BIRD_START_Y = 250
BIRD_WIDTH = 34
BIRD_HEIGHT = 24

# === Pygame Setup ===
pygame.init()
if DRAW_GAME:
    WIN = pygame.display.set_mode((WIN_WIDTH, WIN_HEIGHT))
    pygame.display.set_caption("Flappy Bird AI Training")
else:
    WIN = pygame.Surface((WIN_WIDTH, WIN_HEIGHT))

ASSETS = os.path.join(os.path.dirname(__file__), "assets", "sprites")
bg_img = pygame.image.load(os.path.join(ASSETS, "background-day.png")).convert()
base_img = pygame.image.load(os.path.join(ASSETS, "base.png")).convert_alpha()
pipe_img = pygame.image.load(os.path.join(ASSETS, "pipe-green.png")).convert_alpha()
bird_imgs = [
    pygame.image.load(os.path.join(ASSETS, "bluebird-upflap.png")).convert_alpha(),
    pygame.image.load(os.path.join(ASSETS, "bluebird-midflap.png")).convert_alpha(),
    pygame.image.load(os.path.join(ASSETS, "bluebird-downflap.png")).convert_alpha(),
    pygame.image.load(os.path.join(ASSETS, "redbird-upflap.png")).convert_alpha(),
    pygame.image.load(os.path.join(ASSETS, "redbird-midflap.png")).convert_alpha(),
    pygame.image.load(os.path.join(ASSETS, "redbird-downflap.png")).convert_alpha(),
]
PIPE_TOP = pygame.transform.flip(pipe_img, False, True)
PIPE_BOTTOM = pipe_img

PIPE_ID_COUNTER = 0


def next_pipe_id():
    global PIPE_ID_COUNTER
    PIPE_ID_COUNTER += 1
    return PIPE_ID_COUNTER


class Bird:
    def __init__(self, x, y, is_human=False):
        self.x = float(x)
        self.y = float(y)

        # Match source Player NORMAL mode
        self.vel = -9.0
        self.max_vel = 10.0
        self.min_vel = -8.0
        self.acc = 1.0
        self.flap_acc = -9.0
        self.flapped = False

        self.is_human = is_human
        self.alive = True
        self.frames_alive = 0
        self.img_idx = 0
        self.animation_tick = 0
        self.passed_pipes = set()
        self.img = bird_imgs[0]

        # Match source vertical bounds
        self.min_y = -2 * BIRD_HEIGHT
        self.max_y = FLOOR_Y - BIRD_HEIGHT * 0.75

    @property
    def center_y(self):
        return self.y + BIRD_HEIGHT / 2

    def jump(self):
        if self.y > self.min_y:
            self.vel = self.flap_acc
            self.flapped = True

    def move(self):
        self.frames_alive += 1

        # Match source tick_normal()
        if self.vel < self.max_vel and not self.flapped:
            self.vel += self.acc

        if self.flapped:
            self.flapped = False

        self.y += self.vel

        if self.y < self.min_y:
            self.y = self.min_y
        elif self.y > self.max_y:
            self.y = self.max_y

        self.animation_tick += 1
        if self.animation_tick % 5 == 0:
            self.img_idx = (self.img_idx + 1) % 3

        if self.is_human and CURRENT_MODE == MODE_HUMAN_VS_AI:
            img_set = [3, 4, 5]
        else:
            img_set = [0, 1, 2]

        self.img = bird_imgs[img_set[self.img_idx]]

    def draw(self, win):
        win.blit(self.img, (int(self.x), int(self.y)))

    def rect(self):
        return pygame.Rect(int(self.x), int(self.y), BIRD_WIDTH, BIRD_HEIGHT)

class Pipe:
    def __init__(self, x):
        self.id = next_pipe_id()
        self.x = float(x)
        self.gap_y = random.randint(150, 320)
        self.top = self.gap_y - PIPE_GAP // 2 - PIPE_TOP.get_height()
        self.bottom = self.gap_y + PIPE_GAP // 2
        self.passed_for_score = False

    def move(self):
        self.x -= PIPE_VEL

    def draw(self, win):
        win.blit(PIPE_TOP, (int(self.x), int(self.top)))
        win.blit(PIPE_BOTTOM, (int(self.x), int(self.bottom)))

    def collide(self, bird):
        bird_rect = bird.rect()
        top_rect = pygame.Rect(int(self.x), int(self.top), PIPE_TOP.get_width(), PIPE_TOP.get_height())
        bottom_rect = pygame.Rect(int(self.x), int(self.bottom), PIPE_BOTTOM.get_width(), PIPE_BOTTOM.get_height())
        return bird_rect.colliderect(top_rect) or bird_rect.colliderect(bottom_rect)

    def passed_by(self, bird):
        return self.x + PIPE_TOP.get_width() < bird.x


class Base:
    def __init__(self, y):
        self.y = y
        self.x1 = 0
        self.x2 = base_img.get_width()

    def move(self):
        self.x1 -= PIPE_VEL
        self.x2 -= PIPE_VEL

        if self.x1 + base_img.get_width() < 0:
            self.x1 = self.x2 + base_img.get_width()
        if self.x2 + base_img.get_width() < 0:
            self.x2 = self.x1 + base_img.get_width()

    def draw(self, win):
        win.blit(base_img, (int(self.x1), self.y))
        win.blit(base_img, (int(self.x2), self.y))


class Agent:
    def __init__(self, genome, config, is_human=False):
        self.genome = genome
        self.genome.fitness = 0.0
        self.net = neat.nn.FeedForwardNetwork.create(genome, config)
        self.bird = Bird(BIRD_START_X, BIRD_START_Y, is_human=is_human)
        self.is_human = is_human


def export_genome_to_json(genome, filepath):
    data = {
        "key": genome.key,
        "fitness": genome.fitness,
        "nodes": {},
        "connections": {},
    }

    for node_key, node_gene in genome.nodes.items():
        data["nodes"][str(node_key)] = {
            "bias": node_gene.bias,
            "response": node_gene.response,
            "activation": node_gene.activation,
            "aggregation": node_gene.aggregation,
        }

    for conn_key, conn_gene in genome.connections.items():
        key_str = f"{conn_key[0]},{conn_key[1]}"
        data["connections"][key_str] = {
            "weight": conn_gene.weight,
            "enabled": conn_gene.enabled,
        }

    with open(filepath, "w") as f:
        json.dump(data, f, indent=2)

    print(f"Exported genome to JSON: '{filepath}'")


def load_best_genome(genome_path):
    if not os.path.exists(genome_path):
        print(f"No saved genome found at {genome_path}")
        return None

    with open(genome_path, "rb") as f:
        winner = pickle.load(f)

    print(f"Loaded best genome with fitness: {winner.fitness}")
    return winner


def get_next_pipe(pipes, bird_x):
    for pipe in pipes:
        if pipe.x + PIPE_TOP.get_width() >= bird_x - 5:
            return pipe
    return pipes[0]


def draw_window(win, agents, pipes, base, score, generation, frame_count=0):
    win.blit(bg_img, (0, 0))

    for pipe in pipes:
        pipe.draw(win)

    base.draw(win)

    for agent in agents:
        if agent.bird.alive and not (agent.bird.is_human and CURRENT_MODE == MODE_HUMAN_VS_AI):
            agent.bird.draw(win)

    for agent in agents:
        if agent.bird.alive and agent.bird.is_human:
            agent.bird.draw(win)
            break

    font_small = pygame.font.SysFont("arial", 14)
    alive_count = sum(1 for a in agents if a.bird.alive)

    win.blit(font_small.render(f"Score: {score}", True, (0, 0, 0)), (10, 10))
    win.blit(font_small.render(f"Alive: {alive_count}", True, (0, 0, 0)), (10, 28))
    win.blit(font_small.render(f"Frame: {frame_count}", True, (0, 0, 0)), (10, 46))
    win.blit(font_small.render(f"Gen: {generation}", True, (0, 0, 0)), (10, 64))

    mode_text = "Mode 1: AI only" if CURRENT_MODE == MODE_AI_ONLY else "Mode 2: Human + AI"
    controls_1 = "1=AI only  2=Human+AI  R=Restart"
    controls_2 = "SPACE=Flap human  ESC=Quit"

    win.blit(font_small.render(mode_text, True, (0, 0, 0)), (10, 84))
    win.blit(font_small.render(controls_1, True, (0, 0, 0)), (10, WIN_HEIGHT - 42))
    win.blit(font_small.render(controls_2, True, (0, 0, 0)), (10, WIN_HEIGHT - 24))

    pygame.display.update()


def reset_generation_state():
    global PIPE_ID_COUNTER
    PIPE_ID_COUNTER = 0
    base = Base(FLOOR_Y)
    pipes = [Pipe(WIN_WIDTH + 40)]
    score = 0
    frame_count = 0
    frames_since_score = 0
    return base, pipes, score, frame_count, frames_since_score


def eval_genomes(genomes, config):
    global CURRENT_MODE, RESTART_REQUESTED

    agents = []
    for idx, (_, genome) in enumerate(genomes):
        agents.append(Agent(genome, config, is_human=(idx == 0)))

    base, pipes, score, frame_count, frames_since_score = reset_generation_state()
    clock = pygame.time.Clock()
    generation = getattr(eval_genomes, "generation", 0)

    run = True
    while run and any(agent.bird.alive for agent in agents):
        frame_count += 1
        frames_since_score += 1

        if DRAW_GAME:
            clock.tick(FPS)
        else:
            clock.tick(0)

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.quit()
                raise SystemExit

            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_ESCAPE:
                    pygame.quit()
                    raise SystemExit
                elif event.key == pygame.K_1:
                    CURRENT_MODE = MODE_AI_ONLY
                elif event.key == pygame.K_2:
                    CURRENT_MODE = MODE_HUMAN_VS_AI
                elif event.key == pygame.K_r:
                    RESTART_REQUESTED = True
                elif event.key == pygame.K_SPACE:
                    if CURRENT_MODE == MODE_HUMAN_VS_AI:
                        for agent in agents:
                            if agent.bird.is_human and agent.bird.alive:
                                agent.bird.jump()
                                break

        if RESTART_REQUESTED:
            for agent in agents:
                agent.bird = Bird(BIRD_START_X, BIRD_START_Y, is_human=agent.is_human)
                agent.genome.fitness = 0.0
            base, pipes, score, frame_count, frames_since_score = reset_generation_state()
            RESTART_REQUESTED = False
            continue

        if pipes[-1].x < WIN_WIDTH - PIPE_SPAWN_DISTANCE:
            pipes.append(Pipe(WIN_WIDTH + 20))

        for pipe in pipes:
            pipe.move()

        pipes = [p for p in pipes if p.x > -PIPE_TOP.get_width()]
        base.move()

        alive_agents = [a for a in agents if a.bird.alive]
        if not alive_agents:
            break

        lead_x = max(a.bird.x for a in alive_agents)
        for pipe in pipes:
            if not pipe.passed_for_score and pipe.x + PIPE_TOP.get_width() < lead_x:
                pipe.passed_for_score = True
                score += 1
                frames_since_score = 0

        for agent in agents:
            bird = agent.bird
            genome = agent.genome

            if not bird.alive:
                continue

            next_pipe = get_next_pipe(pipes, bird.x)
            gap_center = next_pipe.gap_y

            dist_x = (next_pipe.x - bird.x) / WIN_WIDTH
            dist_top = (bird.center_y - (gap_center - PIPE_GAP / 2)) / WIN_HEIGHT
            dist_bottom = ((gap_center + PIPE_GAP / 2) - bird.center_y) / WIN_HEIGHT
            vel_norm = bird.vel / 10.0
            y_norm = bird.center_y / WIN_HEIGHT

            inputs = (y_norm, vel_norm, dist_x, dist_top, dist_bottom)

            human_is_active = CURRENT_MODE == MODE_HUMAN_VS_AI and bird.is_human

            if not human_is_active:
                output = agent.net.activate(inputs)[0]
                if output > 0.5:
                    bird.jump()

            bird.move()

            if not human_is_active:
                genome.fitness += 0.02

                if 0 < next_pipe.x - bird.x < 160:
                    alignment = max(0.0, 1.0 - abs(bird.center_y - gap_center) / 120.0)
                    genome.fitness += 0.2 * alignment

                for pipe in pipes:
                    if pipe.id not in bird.passed_pipes and pipe.passed_by(bird):
                        bird.passed_pipes.add(pipe.id)
                        genome.fitness += 20.0

            if bird.y + BIRD_HEIGHT >= FLOOR_Y:
                bird.alive = False
                if not human_is_active:
                    genome.fitness -= 6.0
                continue

            collided = False
            for pipe in pipes:
                if pipe.collide(bird):
                    collided = True
                    break

            if collided:
                bird.alive = False
                if not human_is_active:
                    genome.fitness -= 8.0
                continue

        if DRAW_GAME:
            draw_window(WIN, agents, pipes, base, score, generation, frame_count)

        if frame_count >= MAX_FRAMES_PER_GEN:
            print(f"Generation stopped at max frames ({MAX_FRAMES_PER_GEN})")
            break

        if frames_since_score >= STAGNATION_LIMIT:
            print(f"Generation stopped for stagnation ({STAGNATION_LIMIT} frames without score)")
            break

    for agent in agents:
        if agent.genome.fitness is None:
            agent.genome.fitness = 0.0
        if agent.genome.fitness < 0.0:
            agent.genome.fitness = 0.0


def run_neat(config_path):
    config = neat.Config(
        neat.DefaultGenome,
        neat.DefaultReproduction,
        neat.DefaultSpeciesSet,
        neat.DefaultStagnation,
        config_path,
    )

    genome_path_pkl = os.path.join(os.path.dirname(__file__), "best_genome.pkl")
    genome_path_json = os.path.join(os.path.dirname(__file__), "best_genome.json")

    population = neat.Population(config)
    population.add_reporter(neat.StdOutReporter(True))
    stats = neat.StatisticsReporter()
    population.add_reporter(stats)

    if CONTINUE_FROM_BEST:
        winner = load_best_genome(genome_path_pkl)
        if winner is not None:
            print("ℹSaved genome found, but starting a normal fresh NEAT population is recommended.")

    winner = None
    for gen in range(1, GENERATIONS_TO_TRAIN + 1):
        eval_genomes.generation = gen
        winner = population.run(eval_genomes, 1)

        print(f"\nBest genome after generation {gen}:")
        print(f"Key: {winner.key}")
        print(f"Fitness: {winner.fitness}")

        with open(genome_path_pkl, "wb") as f:
            pickle.dump(winner, f)

        export_genome_to_json(winner, genome_path_json)
        print(f"Saved best genome to '{genome_path_pkl}'\n")

    return winner


if __name__ == "__main__":
    config_path = os.path.join(os.path.dirname(__file__), "neat-config.txt")
    try:
        run_neat(config_path)
    except KeyboardInterrupt:
        print("\n\nTraining stopped by user")
        pygame.quit()
        print("Goodbye!")