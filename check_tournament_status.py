#!/usr/bin/env python3
"""Check tournament status and match details"""
import requests
import json

BASE_URL = "http://localhost:8082"

def api_call(method: str, endpoint: str, data=None, token=None):
    url = f"{BASE_URL}{endpoint}"
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    
    try:
        if method == "GET":
            response = requests.get(url, headers=headers)
        elif method == "POST":
            response = requests.post(url, headers=headers, json=data)
        response.raise_for_status()
        return response.json()
    except Exception as e:
        print(f"Error: {e}")
        return None

# Get token
login = api_call("POST", "/user/login", {
    "email": "john.doe@example.com",
    "password": "password123"
})

token = None
if login and "userData" in login:
    token = login["userData"]["token"]
    print("✓ Authenticated\n")

# Get bracket
bracket = api_call("GET", "/match/2/bracket", token=token)
print("Tournament Bracket:")
print(json.dumps(bracket, indent=2))

# Get individual matches
print("\n\nMatch Details:")
for match_id in [4, 5, 6]:
    match = api_call("GET", f"/match/{match_id}", token=token)
    print(f"\nMatch {match_id}:")
    print(json.dumps(match, indent=2))

