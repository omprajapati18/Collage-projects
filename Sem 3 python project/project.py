import random

# Initialize counters for user wins
rock_paper_scissors_wins = 0
tic_tac_toe_wins = 0

def rock_paper_scissors():
    global rock_paper_scissors_wins
    print("Enter 0 for Rock, 1 for Paper, 2 for Scissors")

    while True:
        try:
            user_input = int(input("Your choice: "))
            if user_input not in [0, 1, 2]:
                print("Invalid choice. Please enter 0, 1, or 2.")
                continue
            break
        except ValueError:
            print("Invalid input. Please enter a number (0, 1, or 2).")

    # Generate computer input (0, 1, or 2)
    computer_input = random.randint(0, 2)

    # Print the choices
    choices = ["Rock", "Paper", "Scissors"]
    print(f"Computer's choice: {choices[computer_input]}")
    print(f"Your choice: {choices[user_input]}")

    # Determine the result
    if user_input == computer_input:
        print("It's a Draw!")
    elif (user_input == 0 and computer_input == 2) or \
         (user_input == 1 and computer_input == 0) or \
         (user_input == 2 and computer_input == 1):
        print("You Win!")
        rock_paper_scissors_wins += 1  # Increment win counter
    else:
        print("You Lose!")

    # Show total wins
    print(f"Total Wins in Rock, Paper, Scissors: {rock_paper_scissors_wins}")

def print_board(board):
    for row in board:
        print(" | ".join(row))
        print("-" * 9)

def have_won(board, player):
    # Check rows
    for row in board:
        if row[0] == player and row[1] == player and row[2] == player:
            return True

    # Check columns
    for col in range(3):
        if board[0][col] == player and board[1][col] == player and board[2][col] == player:
            return True

    # Check diagonals
    if board[0][0] == player and board[1][1] == player and board[2][2] == player:
        return True
    if board[0][2] == player and board[1][1] == player and board[2][0] == player:
        return True

    return False

def tic_tac_toe():
    global tic_tac_toe_wins
    board = [[" " for _ in range(3)] for _ in range(3)]
    player = "X"
    game_over = False
    moves = 0

    while not game_over:
        print_board(board)
        print(f"Player {player}, enter your move (row and column): ", end="")

        try:
            row, col = map(int, input().replace(" ", "").strip())
            if not (0 <= row < 3 and 0 <= col < 3):
                print("Please enter numbers within bounds (0-2).")
                continue
        except ValueError:
            print("Invalid input. Enter two digits separated by a space (e.g., '0 1' or '01').")
            continue

        if board[row][col] == " ":
            board[row][col] = player
            moves += 1
            if have_won(board, player):
                print_board(board)
                print(f"Player {player} has won!")
                if player == "X":  # Only increment for player 'X'
                    tic_tac_toe_wins += 1
                game_over = True
            elif moves == 9:
                print_board(board)
                print("It's a Draw!")
                game_over = True
            else:
                player = "O" if player == "X" else "X"
        else:
            print("Invalid move. Try again!")

    print(f"Total Wins in Tic Tac Toe: {tic_tac_toe_wins}")

def main():
    while True:
        print("\nChoose a game to play:")
        print("1. Rock, Paper, Scissors")
        print("2. Tic Tac Toe")
        print("3. Show Total Wins")
        print("0. Exit")

        choice = input("Enter your choice: ")

        if choice == "1":
            rock_paper_scissors()
        elif choice == "2":
            tic_tac_toe()
        elif choice == "3":
            print("\n===== Total Wins =====")
            print(f"Rock, Paper, Scissors: {rock_paper_scissors_wins}")
            print(f"Tic Tac Toe: {tic_tac_toe_wins}")
        elif choice == "0":
            print("Exiting the game. Goodbye!")
            break
        else:
            print("Invalid choice. Please try again.")

if __name__ == "__main__":
    main()
