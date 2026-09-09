# test_neat.py
import neat
import os

config_path = os.path.join(os.path.dirname(__file__), "neat-config.txt")
try:
    config = neat.Config(
        neat.DefaultGenome,
        neat.DefaultReproduction,
        neat.DefaultSpeciesSet,
        neat.DefaultStagnation,
        config_path,
    )
    print("✅ Config loaded successfully!")
    print(f"📊 Population size: {config.pop_size}")
except Exception as e:
    print(f"❌ Config error: {e}")