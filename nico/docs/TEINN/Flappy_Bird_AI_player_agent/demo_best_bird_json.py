# demo.py - Watch your trained AI bird (JSON version)!
import os
import pygame
import neat
import random
import json

# === Game Constants (MUST match ai.py exactly) ===
WIN_WIDTH = 288
WIN_HEIGHT = 512
FLOOR_Y = 512 - 112
PIPE_GAP = 120  # ✅ Match your ai.py gap size
PIPE_VEL = 3
BIRD_START_X = 50
BIRD_START_Y = 250  # ✅ Match your ai.py start position

# === Load Assets ===
pygame.init()
WIN = pygame.display.set_mode((WIN_WIDTH, WIN_HEIGHT))
pygame.display.set_caption("Flappy Bird AI - Trained Demo (JSON)")

ASSETS = os.path.join(os.path.dirname(__file__), "assets", "sprites")
bg_img = pygame.image.load(os.path.join(ASSETS, "background-day.png")).convert()
base_img = pygame.image.load(os.path.join(ASSETS, "base.png")).convert_alpha()
pipe_img = pygame.image.load(os.path.join(ASSETS, "pipe-green.png")).convert_alpha()
bird_imgs = [
    pygame.image.load(os.path.join(ASSETS, "bluebird-upflap.png")).convert_alpha(),
    pygame.image.load(os.path.join(ASSETS, "bluebird-midflap.png")).convert_alpha(),
    pygame.image.load(os.path.join(ASSETS, "bluebird-downflap.png")).convert_alpha(),
]
PIPE_TOP = pygame.transform.flip(pipe_img, False, True)
PIPE_BOTTOM = pipe_img


# === Bird/Pipe/Base classes (MUST match ai.py exactly) ===
class Bird:
    def __init__(self, x, y):
        self.x = x
        self.y = y
        self.vel = 0  # ✅ Match ai.py
        self.img_idx = 0
        self.img = bird_imgs[0]
        self.alive = True
        
    def jump(self):
        self.vel = -10  # ✅ Match ai.py flap strength
        
    def move(self):
        self.vel += 0.35  # ✅ Match ai.py gravity (floatier)
        self.y += self.vel
        self.img_idx = (self.img_idx + 1) % 3
        self.img = bird_imgs[self.img_idx]
        if self.y < 0:
            self.y = 0
            self.vel = 0
            
    def draw(self, win):
        win.blit(self.img, (self.x, self.y))


class Pipe:
    def __init__(self, x):
        self.x = x
        self.height = random.randint(50, 300)
        #self.gap_y = 250  # ✅ Fixed gap position for demo consistency
        self.top = self.gap_y - PIPE_GAP // 2 - PIPE_TOP.get_height()
        self.bottom = self.gap_y + PIPE_GAP // 2
        self.passed = False
        
    def move(self):
        self.x -= PIPE_VEL
        
    def draw(self, win):
        win.blit(PIPE_TOP, (self.x, self.top))
        win.blit(PIPE_BOTTOM, (self.x, self.bottom))
        
    def collide(self, bird):
        bird_rect = pygame.Rect(bird.x, bird.y, 34, 24)
        top_rect = pygame.Rect(self.x, self.top, PIPE_TOP.get_width(), PIPE_TOP.get_height())
        bottom_rect = pygame.Rect(self.x, self.bottom, PIPE_BOTTOM.get_width(), PIPE_BOTTOM.get_height())
        return bird_rect.colliderect(top_rect) or bird_rect.colliderect(bottom_rect)


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
        win.blit(base_img, (self.x1, self.y))
        win.blit(base_img, (self.x2, self.y))


def import_genome_from_json(filepath, config):
    """✅ Load genome from JSON file (portable format) - FIXED for v1.1.0"""
    with open(filepath, 'r') as f:
        data = json.load(f)
    
    genome_type = config.genome_type
    genome_config = config.genome_config
    
    # ✅ Create new genome (no configure_new to avoid innovation tracker)
    genome = genome_type(int(data['key']))
    
    # ✅ Manually set up the genome structure
    genome.fitness = data.get('fitness', 0)
    
    # Import nodes - create them if they don't exist
    for node_key_str, node_data in data['nodes'].items():
        node_key = int(node_key_str)
        if node_key not in genome.nodes:
            # Node genes don't need innovation numbers
            genome.nodes[node_key] = genome_config.node_gene_type(node_key)
        node_gene = genome.nodes[node_key]
        node_gene.bias = node_data['bias']
        node_gene.response = node_data.get('response', 1.0)
        node_gene.activation = node_data.get('activation', 'tanh')
        node_gene.aggregation = node_data.get('aggregation', 'sum')
    
    # Import connections - create them if they don't exist
    for conn_key_str, conn_data in data['connections'].items():
        in_node, out_node = map(int, conn_key_str.split(','))
        conn_key = (in_node, out_node)
        if conn_key not in genome.connections:
            # ✅ FIXED: Pass dummy innovation number for v1.1.0
            genome.connections[conn_key] = genome_config.connection_gene_type(conn_key, innovation=0)
        conn_gene = genome.connections[conn_key]
        conn_gene.weight = conn_data['weight']
        conn_gene.enabled = conn_data.get('enabled', True)
    
    print(f"✅ Loaded genome from JSON: '{filepath}'")
    print(f"   Fitness: {genome.fitness}")
    print(f"   Nodes: {len(genome.nodes)}, Connections: {len(genome.connections)}")
    return genome


def run_demo(winner, config):
    """Run a single bird using the trained genome"""
    net = neat.nn.FeedForwardNetwork.create(winner, config)
    bird = Bird(BIRD_START_X, BIRD_START_Y)
    base = Base(FLOOR_Y)
    pipes = [Pipe(WIN_WIDTH)]
    score = 0
    clock = pygame.time.Clock()
    
    print("🎮 Demo started! Watch the trained bird fly...")
    
    while bird.alive:
        clock.tick(30)
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                return
        
        # Find closest pipe
        pipe_ind = 0
        if len(pipes) > 1 and bird.x > pipes[0].x + PIPE_TOP.get_width():
            pipe_ind = 1
        
        bird.move()
        
        # Neural network decision (5 inputs, MUST match training)
        pipe = pipes[pipe_ind]
        inputs = (
            bird.y / WIN_HEIGHT,
            bird.vel / 10,
            (pipe.x - bird.x) / WIN_WIDTH,
            (pipe.top - bird.y) / WIN_HEIGHT,
            (pipe.bottom - bird.y) / WIN_HEIGHT,
        )
        output = net.activate(inputs)
        if output[0] > 0.5:  # ✅ Match ai.py threshold
            bird.jump()
        
        # Collisions (instant death on floor)
        if bird.y + 12 >= FLOOR_Y or bird.y < 0:  # ✅ +12 for instant death
            bird.alive = False
        for p in pipes:
            if p.collide(bird):
                bird.alive = False
                break
        if not pipe.passed and pipe.x + PIPE_TOP.get_width() < bird.x:
            pipe.passed = True
            score += 1
            print(f"🎯 Pipe passed! Score: {score}")
        
        # Pipe management
        pipes = [p for p in pipes if p.x > -PIPE_TOP.get_width()]
        if len(pipes) < 2 and pipes[-1].x < WIN_WIDTH - 200:
            pipes.append(Pipe(WIN_WIDTH))
        for p in pipes:
            p.move()
        base.move()
        
        # Draw
        WIN.blit(bg_img, (0, 0))
        for p in pipes:
            p.draw(WIN)
        base.draw(WIN)
        bird.draw(WIN)
        font = pygame.font.SysFont("arial", 24)
        WIN.blit(font.render(f"Score: {score}", True, (255, 255, 255)), (10, 10))
        pygame.display.update()
    
    print(f"🏁 Game Over! Final Score: {score}")
    pygame.time.wait(3000)


if __name__ == "__main__":
    config_path = os.path.join(os.path.dirname(__file__), "neat-config.txt")
    genome_path_json = os.path.join(os.path.dirname(__file__), "best_genome.json")  # ✅ JSON file
    genome_path_pkl = os.path.join(os.path.dirname(__file__), "best_genome.pkl")    # Fallback
    
    # Try JSON first, fallback to pickle
    if os.path.exists(genome_path_json):
        genome_path = genome_path_json
        print(f"📄 Loading from JSON: {genome_path}")
    elif os.path.exists(genome_path_pkl):
        genome_path = genome_path_pkl
        print(f"📄 Loading from pickle: {genome_path}")
        print("⚠️  No JSON found. Run ai.py to export JSON, or use this pickle file.")
    else:
        print("❌ No saved genome found. Run ai.py first!")
        pygame.quit()
        exit()
    
    config = neat.Config(
        neat.DefaultGenome,
        neat.DefaultReproduction,
        neat.DefaultSpeciesSet,
        neat.DefaultStagnation,
        config_path,
    )
    
    # Load genome based on file type
    if genome_path.endswith('.json'):
        winner = import_genome_from_json(genome_path, config)
    else:
        import pickle
        with open(genome_path, 'rb') as f:
            winner = pickle.load(f)
        print(f"✅ Loaded genome from pickle (fitness: {winner.fitness})")
    
    run_demo(winner, config)