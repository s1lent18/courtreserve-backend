# Tournament Simulation Scripts

## Files to Keep

### Main Scripts:
- **`populate_and_simulate_tournament.py`** - Main script that:
  - Populates database with dummy data (users, vendors, courts, teams)
  - Creates and starts a tournament
  - This is the primary script you'll use

- **`simulate_final_match.py`** - Helper script to simulate final matches
  - Useful if you need to complete a tournament that's already started
  - Can be adapted for other match simulations

## Files to Remove

All other files are temporary/experimental and can be deleted:
- Test scripts: `simulate_tournament.py`, `simulate_tournament_complete.py`, `complete_simulation.py`, `check_tournament_status.py`, `verify_data.py`, `run_and_log.py`
- Log files: `execution.log`, `simulation_complete.log`, `script_output.log`
- Output files: `output.txt`, `result.txt`, `simulation_output.txt`, `script_output.txt`, `final_output.txt`, `complete_output.txt`
- Batch files: `run_populate.bat`, `run_complete.bat`
- Bash script: `populate_and_simulate_tournament.sh` (not needed on Windows)

