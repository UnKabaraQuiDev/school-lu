import pygame
import sys

pygame.init()

# Window settings
WIDTH, HEIGHT = 400, 600
window = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("Flappy Bird")

# Colors
WHITE = (255, 255, 255)
BLUE = (0, 0, 255)

# Bird settings
bird_x = 50
bird_y = 300
bird_velocity = 0
gravity = 0.5

clock = pygame.time.Clock()

running = True
while running:
    clock.tick(60)  # 60 FPS
    window.fill(WHITE)

    # Events
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False
        if event.type == pygame.KEYDOWN:
            '''if event.key == pygame.K_SPACE:
                bird_velocity = -10  # flap'''
            if ai_mode:
                inputs = [bird_y, distance_to_pipe, pipe_height]
                bird_ai.think(inputs, net)
            else:
                for event in pygame.event.get():
                    if event.type == pygame.KEYDOWN:
                        if event.key == pygame.K_SPACE:
                            bird_velocity = -10

    # Bird physics
    bird_velocity += gravity
    bird_y += bird_velocity

    # Draw bird
    pygame.draw.circle(window, BLUE, (int(bird_x), int(bird_y)), 20)

    pygame.display.update()

pygame.quit()
sys.exit()