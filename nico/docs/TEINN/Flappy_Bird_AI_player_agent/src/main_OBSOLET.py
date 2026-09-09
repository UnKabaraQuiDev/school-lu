import os
import pygame
import neat
from src.flappy import Flappy
from Project_Flappy_Bird.ai import BirdAI

# Path to NEAT config file
CONFIG_PATH = os.path.join(os.path.dirname(__file__), "neat-config.txt")

# Game settings
MAX_FRAMES = 10000  # optional: stop after this many frames to prevent infinite runs

def run_ai(genomes, config):
    birds = []
    nets = []
    ge = []

    # Create birds, networks, and genomes
    for genome_id, genome in genomes:
        net = neat.nn.FeedForwardNetwork.create(genome, config)
        bird = BirdAI()
        birds.append(bird)
        nets.append(net)
        genome.fitness = 0
        ge.append(genome)

    # Initialize game
    pygame.init()
    flappy_game = Flappy()
    screen = pygame.display.set_mode((flappy_game.config.window.width,
                                      flappy_game.config.window.height))
    clock = flappy_game.config.clock

    # Game entities
    background = flappy_game.background
    floor = flappy_game.floor
    pipes = flappy_game.pipes
    score = flappy_game.score

    frame_count = 0
    run_game = True

    while run_game and len(birds) > 0 and frame_count < MAX_FRAMES:
        frame_count += 1
        # Event handling
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.quit()
                return

        # Game tick
        background.tick()
        floor.tick()
        pipes.tick()
        score.tick()

        # Update birds
        for i, bird in enumerate(birds):
            if not bird.alive:
                continue

            # Inputs: bird y, distance to next pipe x, top pipe y, bottom pipe y
            if len(pipes.upper) > 0:
                next_pipe = pipes.upper[0]
                inputs = [
                    bird.y,
                    next_pipe.x - 50,  # distance to next pipe
                    next_pipe.height,
                    next_pipe.height + pipes.gap,
                ]
                output = nets[i].activate(inputs)
                if output[0] > 0.5:
                    bird.flap()

            bird.move()

            # Collision with floor or ceiling
            if bird.y + 24 >= floor.y or bird.y < 0:
                bird.alive = False
                ge[i].fitness -= 1

            # Collision with pipes
            for pipe_top, pipe_bottom in zip(pipes.upper, pipes.lower):
                if pipe_top.collide(bird) or pipe_bottom.collide(bird):
                    bird.alive = False
                    ge[i].fitness -= 1
                elif not hasattr(pipe_top, 'passed') and pipe_top.x + pipe_top.width < 50:
                    pipe_top.passed = True
                    ge[i].fitness += 5  # reward for passing a pipe

            if bird.alive:
                ge[i].fitness += 0.1  # small reward for surviving

        # Draw everything
        screen.fill((0, 0, 0))
        background.render(screen)
        pipes.render(screen)
        floor.render(screen)

        for bird in birds:
            if bird.alive:
                pygame.draw.circle(screen, (255, 255, 0), (50, int(bird.y)), 12)

        pygame.display.update()
        clock.tick(30)

def eval_genomes(genomes, config):
    run_ai(genomes, config)

def run_neat():
    #print("Loading NEAT config from:", CONFIG_PATH)
    config = neat.Config(
        neat.DefaultGenome,
        neat.DefaultReproduction,
        neat.DefaultSpeciesSet,
        neat.DefaultStagnation,
        CONFIG_PATH,
    )

    # Create the population
    p = neat.Population(config)

    # Add reporters for stdout and statistics
    p.add_reporter(neat.StdOutReporter(True))
    stats = neat.StatisticsReporter()
    p.add_reporter(stats)

    # Run NEAT
    winner = p.run(eval_genomes, 50)  # up to 50 generations
    print("Best genome:", winner)

if __name__ == "__main__":
    run_neat()