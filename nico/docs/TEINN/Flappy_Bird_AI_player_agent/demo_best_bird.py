import os
import json
import pickle
import random
import pygame
import neat

# === Constants: must match ai.py ===
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
FPS = 30

MODE_AI_ONLY = 1
MODE_HUMAN_VS_AI = 2

pygame.init()
WIN = pygame.display.set_mode((WIN_WIDTH, WIN_HEIGHT))
pygame.display.set_caption("Flappy Bird Demo")

ASSETS = os.path.join(os.path.dirname(__file__), "assets", "sprites")
bg_img = pygame.image.load(os.path.join(ASSETS, "background-day.png")).convert()
base_img = pygame.image.load(os.path.join(ASSETS, "base.png")).convert_alpha()
pipe_img = pygame.image.load(os.path.join(ASSETS, "pipe-green.png")).convert_alpha()

bird_imgs = {
    "blue": [
        pygame.image.load(os.path.join(ASSETS, "bluebird-upflap.png")).convert_alpha(),
        pygame.image.load(os.path.join(ASSETS, "bluebird-midflap.png")).convert_alpha(),
        pygame.image.load(os.path.join(ASSETS, "bluebird-downflap.png")).convert_alpha(),
    ],
    "red": [
        pygame.image.load(os.path.join(ASSETS, "redbird-upflap.png")).convert_alpha(),
        pygame.image.load(os.path.join(ASSETS, "redbird-midflap.png")).convert_alpha(),
        pygame.image.load(os.path.join(ASSETS, "redbird-downflap.png")).convert_alpha(),
    ],
}

PIPE_TOP = pygame.transform.flip(pipe_img, False, True)
PIPE_BOTTOM = pipe_img

PIPE_ID_COUNTER = 0


def next_pipe_id():
    global PIPE_ID_COUNTER
    PIPE_ID_COUNTER += 1
    return PIPE_ID_COUNTER


class Bird:
    def __init__(self, x, y, color="blue", is_human=False):
        self.x = float(x)
        self.y = float(y)

        # Match source Player NORMAL mode
        self.vel = -9.0
        self.max_vel = 10.0
        self.min_vel = -8.0
        self.acc = 1.0
        self.flap_acc = -9.0
        self.flapped = False

        self.alive = True
        self.color = color
        self.is_human = is_human
        self.img_idx = 0
        self.animation_tick = 0
        self.img = bird_imgs[self.color][0]
        self.score = 0

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
        self.img = bird_imgs[self.color][self.img_idx]

    def rect(self):
        return pygame.Rect(int(self.x), int(self.y), BIRD_WIDTH, BIRD_HEIGHT)

    def draw(self, win):
        win.blit(self.img, (int(self.x), int(self.y)))


class Pipe:
    def __init__(self, x):
        self.id = next_pipe_id()
        self.x = float(x)
        self.gap_y = random.randint(150, 320)
        self.top = self.gap_y - PIPE_GAP // 2 - PIPE_TOP.get_height()
        self.bottom = self.gap_y + PIPE_GAP // 2
        self.scored_birds = set()

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


def get_next_pipe(pipes, bird_x):
    for pipe in pipes:
        if pipe.x + PIPE_TOP.get_width() >= bird_x - 5:
            return pipe
    return pipes[0]


def import_genome_from_json(filepath, config):
    with open(filepath, "r") as f:
        data = json.load(f)

    genome_type = config.genome_type
    genome_config = config.genome_config

    genome = genome_type(int(data["key"]))
    genome.fitness = data.get("fitness", 0)

    for node_key_str, node_data in data["nodes"].items():
        node_key = int(node_key_str)
        if node_key not in genome.nodes:
            genome.nodes[node_key] = genome_config.node_gene_type(node_key)
        node_gene = genome.nodes[node_key]
        node_gene.bias = node_data["bias"]
        node_gene.response = node_data.get("response", 1.0)
        node_gene.activation = node_data.get("activation", "tanh")
        node_gene.aggregation = node_data.get("aggregation", "sum")

    for conn_key_str, conn_data in data["connections"].items():
        in_node, out_node = map(int, conn_key_str.split(","))
        conn_key = (in_node, out_node)
        if conn_key not in genome.connections:
            genome.connections[conn_key] = genome_config.connection_gene_type(conn_key, innovation=0)

        conn_gene = genome.connections[conn_key]
        conn_gene.weight = conn_data["weight"]
        conn_gene.enabled = conn_data.get("enabled", True)

    print(f"Loaded genome from JSON: {filepath}")
    print(f"Fitness: {genome.fitness}")
    print(f"Nodes: {len(genome.nodes)}, Connections: {len(genome.connections)}")
    return genome


def load_winner(config, base_dir):
    genome_path_json = os.path.join(base_dir, "best_genome.json")
    genome_path_pkl = os.path.join(base_dir, "best_genome.pkl")

    if os.path.exists(genome_path_json):
        return import_genome_from_json(genome_path_json, config)

    if os.path.exists(genome_path_pkl):
        with open(genome_path_pkl, "rb") as f:
            winner = pickle.load(f)
        print(f"Loaded genome from pickle (fitness: {winner.fitness})")
        return winner

    raise FileNotFoundError("No best_genome.json or best_genome.pkl found. Run ai.py first.")


def make_ai_decision(net, bird, pipes):
    next_pipe = get_next_pipe(pipes, bird.x)
    gap_center = next_pipe.gap_y

    dist_x = (next_pipe.x - bird.x) / WIN_WIDTH
    dist_top = (bird.center_y - (gap_center - PIPE_GAP / 2)) / WIN_HEIGHT
    dist_bottom = ((gap_center + PIPE_GAP / 2) - bird.center_y) / WIN_HEIGHT
    vel_norm = bird.vel / 10.0
    y_norm = bird.center_y / WIN_HEIGHT

    inputs = (y_norm, vel_norm, dist_x, dist_top, dist_bottom)
    output = net.activate(inputs)[0]
    if output > 0.5:
        bird.jump()


def reset_round(mode):
    global PIPE_ID_COUNTER
    PIPE_ID_COUNTER = 0

    base = Base(FLOOR_Y)
    pipes = [Pipe(WIN_WIDTH + 40)]
    ai_bird = Bird(BIRD_START_X, BIRD_START_Y, color="blue", is_human=False)
    human_bird = Bird(BIRD_START_X, BIRD_START_Y, color="red", is_human=True) if mode == MODE_HUMAN_VS_AI else None

    return base, pipes, ai_bird, human_bird


def update_bird_score(bird, pipes):
    for pipe in pipes:
        if bird.alive and bird not in pipe.scored_birds and pipe.passed_by(bird):
            pipe.scored_birds.add(bird)
            bird.score += 1


def check_bird_death(bird, pipes):
    if not bird.alive:
        return

    if bird.y + BIRD_HEIGHT >= FLOOR_Y:
        bird.alive = False
        return

    for pipe in pipes:
        if pipe.collide(bird):
            bird.alive = False
            return


def draw_ui(win, mode, ai_bird, human_bird):
    font_small = pygame.font.SysFont("arial", 14)
    font_big = pygame.font.SysFont("arial", 18)

    title = "Mode 1: AI Only" if mode == MODE_AI_ONLY else "Mode 2: Human vs AI"
    win.blit(font_big.render(title, True, (0, 0, 0)), (10, 10))

    if ai_bird:
        win.blit(font_small.render(f"AI Score: {ai_bird.score}", True, (0, 0, 0)), (10, 34))
        ai_state = "alive" if ai_bird.alive else "dead"
        win.blit(font_small.render(f"AI: {ai_state}", True, (0, 0, 0)), (10, 52))

    if human_bird:
        win.blit(font_small.render(f"Human Score: {human_bird.score}", True, (0, 0, 0)), (10, 70))
        human_state = "alive" if human_bird.alive else "dead"
        win.blit(font_small.render(f"Human: {human_state}", True, (0, 0, 0)), (10, 88))

    controls_1 = "1=AI only  2=Human vs AI  R=Restart"
    controls_2 = "SPACE=Flap human  ESC=Quit"
    win.blit(font_small.render(controls_1, True, (0, 0, 0)), (10, WIN_HEIGHT - 42))
    win.blit(font_small.render(controls_2, True, (0, 0, 0)), (10, WIN_HEIGHT - 24))


def draw_window(win, mode, pipes, base, ai_bird, human_bird):
    win.blit(bg_img, (0, 0))

    for pipe in pipes:
        pipe.draw(win)

    base.draw(win)

    if ai_bird and ai_bird.alive:
        ai_bird.draw(win)

    if human_bird and human_bird.alive:
        human_bird.draw(win)

    draw_ui(win, mode, ai_bird, human_bird)
    pygame.display.update()


def run_demo(winner, config):
    net = neat.nn.FeedForwardNetwork.create(winner, config)

    mode = MODE_AI_ONLY
    base, pipes, ai_bird, human_bird = reset_round(mode)
    clock = pygame.time.Clock()

    running = True
    while running:
        clock.tick(FPS)

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False
            elif event.type == pygame.KEYDOWN:
                if event.key == pygame.K_ESCAPE:
                    running = False
                elif event.key == pygame.K_1:
                    mode = MODE_AI_ONLY
                    base, pipes, ai_bird, human_bird = reset_round(mode)
                elif event.key == pygame.K_2:
                    mode = MODE_HUMAN_VS_AI
                    base, pipes, ai_bird, human_bird = reset_round(mode)
                elif event.key == pygame.K_r:
                    base, pipes, ai_bird, human_bird = reset_round(mode)
                elif event.key == pygame.K_SPACE:
                    if mode == MODE_HUMAN_VS_AI and human_bird and human_bird.alive:
                        human_bird.jump()

        if pipes[-1].x < WIN_WIDTH - PIPE_SPAWN_DISTANCE:
            pipes.append(Pipe(WIN_WIDTH + 20))

        for pipe in pipes:
            pipe.move()

        pipes = [p for p in pipes if p.x > -PIPE_TOP.get_width()]
        base.move()

        if ai_bird and ai_bird.alive:
            make_ai_decision(net, ai_bird, pipes)
            ai_bird.move()
            update_bird_score(ai_bird, pipes)
            check_bird_death(ai_bird, pipes)

        if human_bird and human_bird.alive:
            human_bird.move()
            update_bird_score(human_bird, pipes)
            check_bird_death(human_bird, pipes)

        if mode == MODE_AI_ONLY:
            if not ai_bird.alive:
                base, pipes, ai_bird, human_bird = reset_round(mode)
        else:
            if not ai_bird.alive and human_bird and not human_bird.alive:
                base, pipes, ai_bird, human_bird = reset_round(mode)

        draw_window(WIN, mode, pipes, base, ai_bird, human_bird)

    pygame.quit()


if __name__ == "__main__":
    base_dir = os.path.dirname(__file__)
    config_path = os.path.join(base_dir, "neat-config.txt")

    config = neat.Config(
        neat.DefaultGenome,
        neat.DefaultReproduction,
        neat.DefaultSpeciesSet,
        neat.DefaultStagnation,
        config_path,
    )

    winner = load_winner(config, base_dir)
    run_demo(winner, config)