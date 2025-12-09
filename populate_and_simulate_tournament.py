#!/usr/bin/env python3
"""
Script to populate database with dummy data and simulate a tournament.
Application should be running on port 8082.
"""

import requests
import json
from datetime import datetime, timedelta
from typing import Optional, Dict, Any

BASE_URL = "http://localhost:8082"

# Colors for terminal output
class Colors:
    GREEN = '\033[0;32m'
    BLUE = '\033[0;34m'
    YELLOW = '\033[1;33m'
    RED = '\033[0;31m'
    NC = '\033[0m'  # No Color

def print_colored(message: str, color: str = Colors.NC):
    """Print colored message"""
    try:
        print(f"{color}{message}{Colors.NC}")
    except UnicodeEncodeError:
        # Fallback to ASCII for Windows console
        message_ascii = message.replace('[OK]', '[OK]').replace('[FAIL]', '[FAIL]')
        print(f"{color}{message_ascii}{Colors.NC}")

def api_call(method: str, endpoint: str, data: Optional[Dict] = None, token: Optional[str] = None, raise_on_error: bool = True) -> Optional[Dict[str, Any]]:
    """Make API call and return JSON response"""
    url = f"{BASE_URL}{endpoint}"
    headers = {"Content-Type": "application/json"}
    
    if token:
        headers["Authorization"] = f"Bearer {token}"
    
    try:
        if method.upper() == "GET":
            response = requests.get(url, headers=headers)
        elif method.upper() == "POST":
            response = requests.post(url, headers=headers, json=data)
        else:
            raise ValueError(f"Unsupported method: {method}")
        
        response.raise_for_status()
        return response.json()
    except requests.exceptions.HTTPError as e:
        if not raise_on_error:
            return None
        # Re-raise without printing - let caller handle
        raise
    except requests.exceptions.RequestException as e:
        if not raise_on_error:
            return None
        # Re-raise without printing - let caller handle
        raise

def extract_id(response: Dict, key: str = "id") -> Optional[int]:
    """Extract ID from response"""
    if isinstance(response, dict):
        # Try different paths
        if key in response:
            return response[key]
        # Check nested structures
        for value in response.values():
            if isinstance(value, dict) and key in value:
                return value[key]
            elif isinstance(value, list) and value and isinstance(value[0], dict) and key in value[0]:
                return value[0][key]
    return None

def main():
    print_colored("=== Starting Database Population and Tournament Simulation ===\n", Colors.BLUE)
    
    # Step 1: Create Users
    print_colored("Step 1: Creating Users...", Colors.BLUE)
    users = []
    user_data = [
        {"name": "John Doe", "email": "john.doe@example.com", "password": "password123", "location": "Karachi"},
        {"name": "Jane Smith", "email": "jane.smith@example.com", "password": "password123", "location": "Karachi"},
        {"name": "Bob Johnson", "email": "bob.johnson@example.com", "password": "password123", "location": "Karachi"},
        {"name": "Alice Brown", "email": "alice.brown@example.com", "password": "password123", "location": "Karachi"},
        {"name": "Charlie Wilson", "email": "charlie.wilson@example.com", "password": "password123", "location": "Karachi"},
        {"name": "Diana Prince", "email": "diana.prince@example.com", "password": "password123", "location": "Karachi"},
        {"name": "Eve Davis", "email": "eve.davis@example.com", "password": "password123", "location": "Karachi"},
        {"name": "Frank Miller", "email": "frank.miller@example.com", "password": "password123", "location": "Karachi"},
    ]
    
    for user_info in user_data:
        try:
            response = api_call("POST", "/user/register", user_info, raise_on_error=True)
            user_id = extract_id(response)
            if user_id:
                users.append({"id": user_id, **user_info})
                print_colored(f"  [OK] Created User: {user_info['name']} (ID: {user_id})", Colors.GREEN)
            else:
                print_colored(f"  [FAIL] Failed to create user: {user_info['name']}", Colors.RED)
        except requests.exceptions.HTTPError as e:
            if hasattr(e, 'response') and e.response is not None and e.response.status_code == 409:
                # User already exists, login to get ID
                try:
                    login_response = api_call("POST", "/user/login", {
                        "email": user_info["email"],
                        "password": user_info["password"]
                    })
                    if login_response and "userData" in login_response and "id" in login_response["userData"]:
                        user_id = login_response["userData"]["id"]
                        users.append({"id": user_id, **user_info})
                        print_colored(f"  [OK] User already exists: {user_info['name']} (ID: {user_id})", Colors.YELLOW)
                    else:
                        print_colored(f"  [FAIL] Failed to get user ID for: {user_info['name']}", Colors.RED)
                except Exception as login_error:
                    print_colored(f"  [FAIL] Error logging in user {user_info['name']}: {login_error}", Colors.RED)
            else:
                print_colored(f"  [FAIL] Error creating user {user_info['name']}: {e}", Colors.RED)
        except Exception as e:
            print_colored(f"  [FAIL] Error creating user {user_info['name']}: {e}", Colors.RED)
    
    # Create Vendors
    print_colored("\nStep 1b: Creating Vendors...", Colors.BLUE)
    vendors = []
    vendor_data = [
        {"name": "Sports Arena", "email": "sports.arena@example.com", "password": "password123", "location": "Karachi"},
        {"name": "Elite Courts", "email": "elite.courts@example.com", "password": "password123", "location": "Karachi"},
    ]
    
    for vendor_info in vendor_data:
        try:
            response = api_call("POST", "/vendor/register", vendor_info, raise_on_error=True)
            vendor_id = extract_id(response)
            if vendor_id:
                vendors.append({"id": vendor_id, **vendor_info})
                print_colored(f"  [OK] Created Vendor: {vendor_info['name']} (ID: {vendor_id})", Colors.GREEN)
            else:
                print_colored(f"  [FAIL] Failed to create vendor: {vendor_info['name']}", Colors.RED)
        except requests.exceptions.HTTPError as e:
            if hasattr(e, 'response') and e.response is not None and e.response.status_code == 409:
                # Vendor already exists, login to get ID
                try:
                    login_response = api_call("POST", "/vendor/login", {
                        "email": vendor_info["email"],
                        "password": vendor_info["password"]
                    })
                    if login_response and "vendorData" in login_response and "id" in login_response["vendorData"]:
                        vendor_id = login_response["vendorData"]["id"]
                        vendors.append({"id": vendor_id, **vendor_info})
                        print_colored(f"  [OK] Vendor already exists: {vendor_info['name']} (ID: {vendor_id})", Colors.YELLOW)
                    else:
                        print_colored(f"  [FAIL] Failed to get vendor ID for: {vendor_info['name']}", Colors.RED)
                except Exception as login_error:
                    print_colored(f"  [FAIL] Error logging in vendor {vendor_info['name']}: {login_error}", Colors.RED)
            else:
                print_colored(f"  [FAIL] Error creating vendor {vendor_info['name']}: {e}", Colors.RED)
        except Exception as e:
            print_colored(f"  [FAIL] Error creating vendor {vendor_info['name']}: {e}", Colors.RED)
    
    if not users or not vendors:
        print_colored("\n[FAIL] Failed to create required users/vendors. Exiting.", Colors.RED)
        return
    
    # Step 2: Login to get tokens
    print_colored("\nStep 2: Logging in to get tokens...", Colors.BLUE)
    user_tokens = {}
    vendor_tokens = {}
    
    # Login first user
    try:
        login_response = api_call("POST", "/user/login", {
            "email": users[0]["email"],
            "password": users[0]["password"]
        })
        if "userData" in login_response and "token" in login_response["userData"]:
            user_tokens[users[0]["id"]] = login_response["userData"]["token"]
            print_colored(f"  [OK] Got token for {users[0]['name']}", Colors.GREEN)
    except Exception as e:
        print_colored(f"  [FAIL] Error logging in user: {e}", Colors.RED)
    
    # Login first vendor
    try:
        vendor_login_response = api_call("POST", "/vendor/login", {
            "email": vendors[0]["email"],
            "password": vendors[0]["password"]
        })
        if "vendorData" in vendor_login_response and "token" in vendor_login_response["vendorData"]:
            vendor_tokens[vendors[0]["id"]] = vendor_login_response["vendorData"]["token"]
            print_colored(f"  [OK] Got token for {vendors[0]['name']}", Colors.GREEN)
    except Exception as e:
        print_colored(f"  [FAIL] Error logging in vendor: {e}", Colors.RED)
    
    if not vendor_tokens:
        print_colored("\n[FAIL] Failed to get vendor token. Exiting.", Colors.RED)
        return
    
    vendor_id = vendors[0]["id"]
    vendor_token = vendor_tokens[vendor_id]
    
    # Step 3: Create Courts
    print_colored("\nStep 3: Creating Courts...", Colors.BLUE)
    courts = []
    court_data = [
        {
            "name": "Basketball Court 1",
            "description": "Professional basketball court with wooden floor",
            "location": "Karachi",
            "price": 5000,
            "openTime": "06:00:00",
            "closeTime": "22:00:00",
            "type": "BASKETBALL"
        },
        {
            "name": "Football Field 1",
            "description": "Standard football field with grass",
            "location": "Karachi",
            "price": 8000,
            "openTime": "06:00:00",
            "closeTime": "22:00:00",
            "type": "FOOTBALL"
        },
    ]
    
    for court_info in court_data:
        try:
            response = api_call("POST", f"/court/{vendor_id}/addCourt", court_info, vendor_token)
            court_id = extract_id(response)
            if court_id:
                courts.append({"id": court_id, **court_info})
                print_colored(f"  [OK] Created Court: {court_info['name']} (ID: {court_id})", Colors.GREEN)
            else:
                print_colored(f"  [FAIL] Failed to create court: {court_info['name']}", Colors.RED)
        except Exception as e:
            print_colored(f"  [FAIL] Error creating court {court_info['name']}: {e}", Colors.RED)
    
    if not courts:
        print_colored("\n[FAIL] Failed to create courts. Exiting.", Colors.RED)
        return
    
    court_id = courts[0]["id"]
    
    # Step 4: Create Teams
    print_colored("\nStep 4: Creating Teams...", Colors.BLUE)
    teams = []
    team_data = [
        {"name": "Thunder Bolts", "sport": "BASKETBALL", "captain": users[0], "members": [users[0], users[1]]},
        {"name": "Lightning Strikes", "sport": "BASKETBALL", "captain": users[2], "members": [users[2], users[3]]},
        {"name": "Fire Dragons", "sport": "BASKETBALL", "captain": users[4], "members": [users[4], users[5]]},
        {"name": "Ice Warriors", "sport": "BASKETBALL", "captain": users[6], "members": [users[6], users[7]]},
    ]
    
    for team_info in team_data:
        try:
            # Get token for the captain
            captain_id = team_info['captain']['id']
            captain_token = user_tokens.get(captain_id)
            if not captain_token:
                # Login to get token for this captain
                try:
                    login_response = api_call("POST", "/user/login", {
                        "email": team_info['captain']["email"],
                        "password": team_info['captain']["password"]
                    })
                    if login_response and "userData" in login_response and "token" in login_response["userData"]:
                        captain_token = login_response["userData"]["token"]
                        user_tokens[captain_id] = captain_token
                except Exception as login_error:
                    print_colored(f"  [FAIL] Error getting token for captain: {login_error}", Colors.RED)
                    continue
            
            member_ids = [m["id"] for m in team_info["members"]]
            try:
                response = api_call("POST", f"/team/createTeam?captainId={captain_id}", {
                    "name": team_info["name"],
                    "sport": team_info["sport"],
                    "memberIds": member_ids
                }, captain_token)
                team_id = extract_id(response)
                if team_id:
                    teams.append({"id": team_id, **team_info})
                    print_colored(f"  [OK] Created Team: {team_info['name']} (ID: {team_id})", Colors.GREEN)
                else:
                    print_colored(f"  [FAIL] Failed to create team: {team_info['name']}", Colors.RED)
            except requests.exceptions.HTTPError as e:
                if hasattr(e, 'response') and e.response is not None and e.response.status_code == 409:
                    # Team name already exists, use a unique name
                    import time
                    unique_name = f"{team_info['name']} {int(time.time())}"
                    try:
                        response = api_call("POST", f"/team/createTeam?captainId={captain_id}", {
                            "name": unique_name,
                            "sport": team_info["sport"],
                            "memberIds": member_ids
                        }, captain_token)
                        team_id = extract_id(response)
                        if team_id:
                            team_info_copy = team_info.copy()
                            team_info_copy["name"] = unique_name
                            teams.append({"id": team_id, **team_info_copy})
                            print_colored(f"  [OK] Created Team with unique name: {unique_name} (ID: {team_id})", Colors.GREEN)
                        else:
                            print_colored(f"  [FAIL] Failed to create team with unique name: {team_info['name']}", Colors.RED)
                    except Exception as unique_error:
                        print_colored(f"  [FAIL] Could not create team with unique name: {team_info['name']} - {unique_error}", Colors.RED)
                else:
                    print_colored(f"  [FAIL] Error creating team {team_info['name']}: {e}", Colors.RED)
            except Exception as e:
                print_colored(f"  [FAIL] Error creating team {team_info['name']}: {e}", Colors.RED)
        except Exception as e:
            print_colored(f"  [FAIL] Error processing team {team_info['name']}: {e}", Colors.RED)
    
    if len(teams) < 4:
        print_colored("\n[FAIL] Need at least 4 teams for tournament. Exiting.", Colors.RED)
        return
    
    # Step 5: Create Tournament
    print_colored("\nStep 5: Creating Tournament...", Colors.BLUE)
    start_date = (datetime.now() + timedelta(days=1)).strftime("%Y-%m-%d")
    end_date = (datetime.now() + timedelta(days=8)).strftime("%Y-%m-%d")
    team_ids = [t["id"] for t in teams]
    
    tournament_data = {
        "name": "Basketball Championship 2024",
        "courtId": court_id,
        "startDate": start_date,
        "endDate": end_date,
        "prize": 50000,
        "eliminationType": "SINGLE",
        "isAutoMode": False,
        "teamIds": team_ids,
        "entrance": "FREE"
    }
    
    try:
        tournament_response = api_call("POST", f"/tournament/createTournament?organizerId={vendor_id}", 
                                      tournament_data, vendor_token)
        tournament_id = extract_id(tournament_response)
        if tournament_id:
            print_colored(f"  [OK] Created Tournament: Basketball Championship 2024 (ID: {tournament_id})", Colors.GREEN)
        else:
            print_colored("  [FAIL] Failed to create tournament", Colors.RED)
            return
    except Exception as e:
        print_colored(f"  [FAIL] Error creating tournament: {e}", Colors.RED)
        return
    
    # Step 6: Confirm Tournament
    print_colored("\nStep 6: Confirming Tournament...", Colors.BLUE)
    try:
        confirm_response = api_call("POST", f"/tournament/{tournament_id}/confirmTournament", None, vendor_token)
        print_colored("  [OK] Tournament confirmed", Colors.GREEN)
    except Exception as e:
        print_colored(f"  [FAIL] Error confirming tournament: {e}", Colors.RED)
        return
    
    # Step 7: Start Tournament (Generate Bracket)
    print_colored("\nStep 7: Starting Tournament (Generating Bracket)...", Colors.BLUE)
    try:
        start_response = api_call("POST", f"/tournament/{tournament_id}/startTournament", None, vendor_token)
        print_colored("  [OK] Tournament started - Bracket generated", Colors.GREEN)
    except Exception as e:
        print_colored(f"  [FAIL] Error starting tournament: {e}", Colors.RED)
        return
    
    # Step 8: Get Tournament Bracket
    print_colored("\nStep 8: Getting Tournament Bracket...", Colors.BLUE)
    try:
        bracket_response = api_call("GET", f"/match/{tournament_id}/bracket")
        print_colored("  [OK] Bracket retrieved", Colors.GREEN)
        
        # Extract matches from bracket
        matches = []
        if "bracket" in bracket_response:
            bracket = bracket_response["bracket"]
            if isinstance(bracket, dict):
                # Try to find matches in different possible structures
                for key, value in bracket.items():
                    if isinstance(value, list):
                        for item in value:
                            if isinstance(item, dict) and "id" in item:
                                matches.append(item)
                    elif isinstance(value, dict) and "id" in value:
                        matches.append(value)
            elif isinstance(bracket, list):
                matches = bracket
        
        print_colored(f"  Found {len(matches)} matches in bracket", Colors.YELLOW)
        
        # Step 9: Schedule and Simulate Matches
        print_colored("\nStep 9: Scheduling and Simulating Matches...", Colors.BLUE)
        
        # Get unscheduled matches
        try:
            unscheduled_response = api_call("GET", f"/match/{tournament_id}/unscheduledMatches")
            unscheduled_matches = []
            if "matches" in unscheduled_response:
                unscheduled_matches = unscheduled_response["matches"] if isinstance(unscheduled_response["matches"], list) else []
            
            print_colored(f"  Found {len(unscheduled_matches)} unscheduled matches", Colors.YELLOW)
            
            # Schedule and simulate first few matches
            for i, match in enumerate(unscheduled_matches[:4]):  # Process first 4 matches
                match_id = match.get("id")
                if not match_id:
                    continue
                
                # Schedule match
                schedule_time = (datetime.now() + timedelta(days=1, hours=10+i)).strftime("%Y-%m-%dT%H:%M:%S")
                end_time = (datetime.now() + timedelta(days=1, hours=11+i)).strftime("%Y-%m-%dT%H:%M:%S")
                
                try:
                    schedule_response = api_call("POST", "/match/schedule", {
                        "matchId": match_id,
                        "startTime": schedule_time,
                        "endTime": end_time
                    })
                    print_colored(f"  [OK] Scheduled match {match_id}", Colors.GREEN)
                    
                    # Simulate match result
                    team1_id = match.get("team1Id") or match.get("team1", {}).get("id") if isinstance(match.get("team1"), dict) else None
                    team2_id = match.get("team2Id") or match.get("team2", {}).get("id") if isinstance(match.get("team2"), dict) else None
                    winner_id = team1_id if i % 2 == 0 else team2_id  # Alternate winners
                    
                    if winner_id:
                        update_response = api_call("POST", "/match/updateResult", {
                            "matchId": match_id,
                            "winnerTeamId": winner_id,
                            "team1Score": "85" if i % 2 == 0 else "72",
                            "team2Score": "72" if i % 2 == 0 else "85",
                            "remarks": f"Great match! Team {winner_id} wins."
                        })
                        print_colored(f"  [OK] Updated result for match {match_id} - Winner: Team {winner_id}", Colors.GREEN)
                except Exception as e:
                    print_colored(f"  [FAIL] Error processing match {match_id}: {e}", Colors.RED)
        except Exception as e:
            print_colored(f"  [FAIL] Error getting unscheduled matches: {e}", Colors.RED)
    
    except Exception as e:
        print_colored(f"  [FAIL] Error getting bracket: {e}", Colors.RED)
    
    print_colored("\n=== Database Population and Tournament Simulation Complete! ===", Colors.GREEN)
    print_colored(f"Tournament ID: {tournament_id}", Colors.YELLOW)
    print_colored(f"You can view the tournament bracket at: {BASE_URL}/match/{tournament_id}/bracket", Colors.YELLOW)

if __name__ == "__main__":
    import sys
    # Also write to file for verification
    log_file = open('execution.log', 'w', encoding='utf-8')
    
    class Tee:
        def __init__(self, *files):
            self.files = files
        def write(self, obj):
            for f in self.files:
                f.write(obj)
                f.flush()
        def flush(self):
            for f in self.files:
                f.flush()
    
    # Tee output to both console and file
    sys.stdout = Tee(sys.stdout, log_file)
    sys.stderr = Tee(sys.stderr, log_file)
    
    try:
        main()
        print("\n✓ Script completed successfully!")
    except KeyboardInterrupt:
        print_colored("\n\nScript interrupted by user.", Colors.YELLOW)
    except Exception as e:
        print_colored(f"\n\nUnexpected error: {e}", Colors.RED)
        import traceback
        traceback.print_exc()
    finally:
        log_file.close()
        sys.stdout = sys.__stdout__
        sys.stderr = sys.__stderr__

