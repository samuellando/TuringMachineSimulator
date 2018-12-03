#include <stdlib.h>
#include <stdio.h>

int simulate(char**, int, int, int, int, int, char*, int, int, int, int);

// Function converts a single hexadecimal digit to and integer value.
int hex2int(char ch) {
    if (ch >= '0' && ch <= '9')
        return ch - '0';
    if (ch >= 'A' && ch <= 'F')
        return ch - 'A' + 10;
    if (ch >= 'a' && ch <= 'f')
        return ch - 'a' + 10;
    return -1;
}

// Loads in the data form the input file and crates the turing machine reprisentation
// Verifies that the vachine is valid and begins the simulation.
int main(int argc, char *argv[]) {
    // Check the there is a single input file to the entrepeter.
    if (argc != 2) {
        printf("Invalid number of arguments.\n");
        return -1;
    }
    // Create a small read buffer and create the file pointer.
    char buffer[8];
    FILE *fp;
    fp = fopen(argv[1], "r");
    if (!fp) {
      perror("Error opening file");
      return(-1);
    }
    // Read in the step parameter.
    fgets(buffer, 9, fp); // Jump to the data
    fgets(buffer, 3, fp); // Read the HEX char.
    int step = hex2int(buffer[0]); // Convert to Int.
    // Read the tapesize parameter.
    fgets(buffer, 9, fp);
    fgets(buffer, 4, fp);
    int tapeSize = hex2int(buffer[0])*16+hex2int(buffer[1]);
    // Read the loadPos parameter.
    fgets(buffer, 9, fp);
    fgets(buffer, 4, fp);
    int loadPos = hex2int(buffer[0])*16+hex2int(buffer[1]);
    //Read in the states.
    fgets(buffer, 9, fp); // Jump to the data.
    char *states = malloc(255*sizeof(char)); // Allocate a good amount of memory.
    if (!states) return 1; // Check that the memory is proerly alocated.
    int numStates = 0; // Start counting the number of states.
    do  { 
        fgets(buffer, 3, fp);// Read the state.
        states[numStates] = buffer[0]; // Save the state.
        numStates++; // Increment the counter.
    } while (buffer[1] != '\n'); // If the end of the line is reached.
    states = realloc(states, numStates*sizeof(char)); // Deallocate unuesd memory.
    // Read the alphabet.
    fgets(buffer, 9, fp);
    char *sigma = malloc(255*sizeof(char));
    if (!sigma) return 1;
    int numSigma = 0;
    do  { 
		fgets(buffer, 3, fp);
        sigma[numSigma] = buffer[0];
        numSigma++;
    } while (buffer[1] != '\n');
    sigma = realloc(sigma, numSigma*sizeof(char));
    // Read in the tape alphabet.
    fgets(buffer, 9, fp);
    char *gamma = malloc(255*sizeof(char));
    if (!gamma) return 1;
    int numGamma = 0;
    do  { 
		fgets(buffer, 3, fp);
        gamma[numGamma] = buffer[0];
        numGamma++;
    } while (buffer[1] != '\n');
    gamma = realloc(gamma, numGamma*sizeof(char));
    // Read in the initial state.
    fgets(buffer, 9, fp);
    fgets(buffer, 3, fp);
    char q_0 = buffer[0];
    // Read the blank character.
    fgets(buffer, 9, fp);
    fgets(buffer, 3, fp);
    char blank = buffer[0];
    // Read in the final states.
    fgets(buffer, 9, fp);
    char *final = malloc(255*sizeof(char));
    if (!final) return 1;
    int numFinal = 0;
    do  { 
		fgets(buffer, 3, fp);
        final[numFinal] = buffer[0];
        numFinal++;
    } while (buffer[1] != '\n');
    final = realloc(final, numFinal*sizeof(char));
    // Read in the word.
    fgets(buffer, 9, fp);
    char *word = malloc(255*sizeof(char));
    if (!word) return 1;
    int numWord = 0;
    do  { 
		fgets(buffer, 2, fp);
        word[numWord] = buffer[0];
        numWord++;
    } while (buffer[0] != '\n');
    numWord--; // Remove the trailing new line char.
    word = realloc(word, numWord*sizeof(char));
    // Read in the transition function.
    char *delta = malloc(2040*sizeof(char));
    if (!delta) return 1;
    int numDelta = 0;
    while (fgets(buffer, 8, fp) != NULL) {
        for (int i = 0; i < 6; i++) {
            delta[numDelta] = buffer[i];
            numDelta++;
        }
    }
    delta = realloc(delta, numDelta*sizeof(char));
    fclose(fp); // Close the input stream.
    // Create the turing machine.
    char *m[] = {states, sigma, gamma, delta, &q_0, &blank, final};
    char **M = m; // Pointer to the machine.
    
    simulate(M, numStates, 
                numSigma, 
                numGamma, 
                numDelta, 
                numFinal, 
                word, 
                numWord, 
                tapeSize, 
                loadPos, 
                step);
    free(states);
    free(sigma);
    free(gamma);
    free(delta);
    free(final);
}

int simulate(char **M, int states, int sigma, int gamma, int delta, int finalStates, char *word, int wordsize, int tapesize, int startPos, int step) {
    // Print out the info
    printf("\n---- MACHINE INFO ----\n\n");
    printf("States: ");
    for (int i = 0; i < states; i++) {
        printf("%c ", *(*(M) + i));
    }
    printf("(%d)", states);
    printf("\n\nAlphabet: ");
    for (int i = 0; i < sigma; i++) {
        printf("%c ", *(*(M + 1) + i));
    }
    printf("(%d)", sigma);
    printf("\n\nTape Alphabet: ");
    for (int i = 0; i < gamma; i++) {
        printf("%c ", *(*(M + 2) + i));
    }
    printf("(%d)", gamma);
    printf("\n\nTransitions:\n");
    int j = 0;
    for (int i = 0; i < delta; i++) {
        if (j == 6) {
            printf("\n");
            j = 0;
        }
        printf("%c", *(*(M + 3) + i));
        j++;
    }
    printf("\n(%d)", delta/6);
    printf("\n\nInitial State: %c", *(*(M + 4)));
    printf("\n\nBlank: %c", *(*(M + 5)));
    printf("\n\nFinal States: ");
    for (int i = 0; i < finalStates; i++) {
        printf("%c ", *(*(M + 6) + i));
    }
    printf("(%d)", finalStates);
    printf("\n\nInput: ");
    for (int i = 0; i < wordsize; i++) {
        printf("%c", *(word + i));
    }
    printf(" (%d)", wordsize);
    printf("\n\n");
    // Check if the macine is valid:
    int valid = 1;
    printf("---- MACHINE VALIDITY ----\n\n");
    // Transitions
    for (int i = 0; i < delta; i += 6) {
        for (int k = 0; k < 6; k++) {
            if (k == 2 && (*(*(M + 3) + i + k)) != '=') {
                printf("Invalid transition %d '=' expected at position 2, got '%c'\n\n", i/6, *(*(M + 3) + i + k));
                valid = 0;
            }
            else if (k == 5 && (*(*(M + 3) + i + k) != 'L') && (*(*(M + 3) + i + k) != 'R')) {
                printf("Invalid tranition %d 'R' or 'L expected at position 5, got '%c'\n\n", i/6, *(*(M + 3) + i + k));
                valid = 0;
            }
            else if (k == 0 || k == 3)
                for (int j = 0; j < states; j++) {
                    if (*(*(M) + j) == *(*(M + 3) + i + k)) 
                        break;
                    if (j == states - 1){
                        printf("Invalid state '%c' in transition %d position %d\n\n", *(*(M + 3) + i + k), i/6, k);
                        valid = 0;
                    }
                }
            else if (k == 1 || k == 4)
                for (int j = 0; j < gamma; j++) {
                    if (*(*(M + 2) + j) == *(*(M + 3) + i + k)) 
                        break;
                    if (j == gamma - 1){
                        printf("Invalid character '%c' in transition %d position %d\n\n", *(*(M + 3) + i + k), i/6, k);
                        valid = 0;
                    }
                }
        }
    }
    // Initial state
    for (int j = 0; j < states; j++) {
        if (*(*(M) + j) == *(*(M + 4)))
            break;
        if (j == states - 1) {
            printf("Invalid initial state '%c'\n\n", *(*(M + 4)));
            valid = 0;
        }
    }
    // Blank
    for (int j = 0; j < gamma; j++) {
        if (*(*(M + 2) + j) == *(*(M + 5)))
            break;
        if (j == gamma - 1) {
            printf("Invalid blank '%c'\n\n", *(*(M + 5)));
            valid = 0;
        }
    }
    // Final states.
    for (int i = 0; i < finalStates; i++) {
        for (int j = 0; j < states; j++) {
            if (*(*(M) + j) == *(*(M + 6) + i))
                break;
            if (j == states - 1) {
                printf("Invalid final state '%c'\n\n", *(*(M + 6) + i));
                valid = 0;
            }
        }
    }
    // Word.
    for (int i = 0; i < wordsize; i++) {
        for (int j = 0; j < sigma; j++) {
            if (*(*(M + 1) + j) == *(word + i)) 
                break;
            if (j == sigma-1) {
                printf("Invalid character '%c' in input!\n\n", *(word + i));
                valid = 0;
            }
        }
    }
    // Word load location.
    if (wordsize + startPos > tapesize) {
        printf("Invalid tape load\n\n");
        valid = 0;
    }
    if (valid) printf("valid\n\n");
    else return 1;

    // Create the tape.
    char *tape = malloc(tapesize);
    for (int i = 0; i < tapesize; i++) {
        *(tape + i) = *(*(M + 5));
    }
    // Load the tape.
    for (int i = 0; i < wordsize; i++) {
        *(tape + startPos + i) = *(word + i);
    }

    // Run the machine.
    int run = 1;
    printf("---- RUNNING ----\n");
    printf("\nTransition: START\n");
    while (run) {
        // Print the tape
        for (int i = 0; i < tapesize; i++) {
            printf("%c", *(tape + i));
        }
        printf("\n");
        for (int i = 0; i < tapesize; i++) {
            if (i == startPos) {
                printf("^%c\n", *(*(M + 4)));
                break;
            }
            else
                printf(" ");
        }
        if (step) {
            if (step == 1) {
                getchar();
            }
            else {
                volatile long i=0;
                for (i = 0; i < step*1000000; i++);
                printf("\n");
            }
            printf("\033[F\033[F\033[2K\033[F\033[2K\033[F\033[2K\033[F");
        }
        // Apply next transition.
        for (int i = 0; i < delta; i += 6) {
            if (*(*(M + 3) + i) == *(*(M + 4))) {
                if (*(*(M + 3) + i + 1) == *(tape + startPos)) {
                    *(*(M + 4)) = *(*(M + 3) + i + 3);
                    *(tape + startPos) = *(*(M + 3) + i + 4);
                    if (*(*(M + 3) + i + 5) == 'R') startPos++;
                    else startPos--;
                    printf("\nTransition: %d\n", i/6);
                    break;
                }
            }
            if (i == delta - 6) {
                run = 0;
                printf("\nTransition: HALT\n");
                for (int i = 0; i < tapesize; i++) {
                    printf("%c", *(tape + i));
                }
                printf("\n");
                for (int i = 0; i < tapesize; i++) {
                    if (i == startPos) {
                        printf("^%c\n", *(*(M + 4)));
                        break;
                    }
                    else
                        printf(" ");
                }
                printf("\n");
            } 
        }
    }
    printf("---- RESULT ----");
    for (int i = 0; i < finalStates; i++) {
        if (*(*(M + 6) + i) == *(*(M + 4))) {
            printf("\n\nACCEPT\n\n");
            break;
        } 
        if (i == finalStates - 1) printf("\n\nREJECT\n\n");
    }
    free(tape);
    return 0;
}