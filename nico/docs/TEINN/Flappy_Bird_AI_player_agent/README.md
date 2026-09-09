# Flappy Bird AI

Simple Flappy Bird project with an AI trained using NEAT.

## Setup

Create new virtual environment:
```bash
python -m venv venv
```

Activate the new wvirtual environment:
```bash
.\venv\Scripts\Activate.ps1
```

Install dependencies:
```bash
pip install -r requirements.txt
```

Expose kernel:
```bash
python -m ipykernel install --user --name code-detector-env --display-name "Python (Flappy Bird AI Player Agent)"
```

## Run the Game

```bash
python -m src.flappy
```

### Controls (Game)
* SPACE -> start
* SPACE -> flap
* ESC -> quit  

## Run the AI

```bash
python ai.py
```

### Controls (AI)
* 1 -> AI mode  
* 2 -> Human + AI  
* R -> restart  
* ESC -> quit  
* SPACE -> flap

## Run the Demo

```bash
python demo_best_bird.py
```

### Controls (Demo)
* 1 -> AI mode  
* 2 -> Human + AI  
* R -> restart  
* ESC -> quit  
* SPACE -> flap
