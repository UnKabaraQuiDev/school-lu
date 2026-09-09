# demo.py - Watch your trained AI bird!
import os
import pygame
import neat
import pickle

# === Game Constants (must match ai.py) ===
WIN_WIDTH = 288
WIN_HEIGHT = 512
FLOOR_Y = 512 - 112
PIPE_GAP = 100
PIPE_VEL = 3
BIRD_START_X = 50
BIRD_START_Y = 200

# === Load Assets ===
pygame.init()
WIN = pygame.display.set_mode((WIN_WIDTH, WIN_HEIGHT))
pygame.display.set_caption("Flappy Bird AI - Trained Demo")

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

# === Bird/Pipe/Base classes (copy from ai.py) ===
class Bird:
    def __init__(self, x, y):
        self.x = x
        self.y = y
        self.vel = -2
        self.img_idx = 0
        self.img = bird_imgs[0]
        self.alive = True
    def jump(self):
        self.vel = -10
    def move(self):
        self.vel += 0.5
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
        self.height = __import__('random').randint(50, 300)
        self.top = self.height - PIPE_TOP.get_height()
        self.bottom = self.height + PIPE_GAP
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
        
        # Neural network decision (5 inputs, matching training)
        pipe = pipes[pipe_ind]
        inputs = (
            bird.y / WIN_HEIGHT,
            bird.vel / 10,
            (pipe.x - bird.x) / WIN_WIDTH,
            (pipe.top - bird.y) / WIN_HEIGHT,
            (pipe.bottom - bird.y) / WIN_HEIGHT,
        )
        output = net.activate(inputs)
        if output[0] > 0.0:  # Same threshold as training
            bird.jump()
        
        # Collisions
        if bird.y + 24 >= FLOOR_Y or bird.y < 0:
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
    genome_path = os.path.join(os.path.dirname(__file__), "best_genome.pkl")
    
    if not os.path.exists(genome_path):
        print("❌ No saved genome found. Run ai.py first!")
    else:
        config = neat.Config(
            neat.DefaultGenome, neat.DefaultReproduction,
            neat.DefaultSpeciesSet, neat.DefaultStagnation,
            config_path,
        )
        with open(genome_path, 'rb') as f:
            winner = pickle.load(f)
        print(f"✅ Loaded genome with fitness: {winner.fitness}")
        run_demo(winner, config)