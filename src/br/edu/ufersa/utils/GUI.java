package br.edu.ufersa.utils;

public class GUI {

    public static void loginScreen() {
        System.out.println("""
             ===================================
            |                                   |
            |       Hey! Welcome to the         |
            |   Car Dealership System \\(^u^)/   |
            |                                   |
            |    Please, log-in to continue!    |
            |                                   |
             ===================================
            """);
    }

    public static void clientMenu() {
        System.out.print("""
             ============ Main Menu ============
            |   | [1] |   Search car            |
            Z   | [2] |   List all cars         Z
            |   | [3] |   Check stock           |
            Z   | [4] |   Buy a car             Z
            |   | [5] |   Exit                  |
             ===================================

            Option: """);
    }

    public static void employeeMenu() {
        System.out.print("""
             ============ Main Menu ============
            |   | [1] |   Search car            |
            Z   | [2] |   List all cars         Z
            |   | [3] |   Check stock           |
            Z   | [4] |   Buy a car             Z
            |   | [5] |   Add a car             |
            Z   | [6] |   Update a car          Z
            |   | [7] |   Remove a car          |
            Z   | [8] |   Exit                  Z
             ===================================

            Option: """);
    }

    public static void searchOps() {
        System.out.print("""
            Searching by...?
            [1]    -    Renavam
            [2]    -    Name

            Option: """);
    }

    public static void buyOps() {
        System.out.print("""
            What car do you want to check...?
            
            Name: """);
    }

    public static void listOps() {
        System.out.print("""
            Listing by...?
            [1]    -    General
            [2]    -    Category

            Option: """);
    }
    
    public static void categoryOps() {
        System.out.print("""
            [0]    -    Economy
            [1]    -    Intermediate
            [2]    -    Executive
            
            Categoria: """);
        }
        
    public static void stockOps() {
        System.out.print("""
            Checking stock by...?
            [1]    -    General
            [2]    -    Name

            Option: """);
    }

    public static void clearScreen() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                // Se estiver no Windows, usa o comando "cls" para limpar o console.
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Se estiver em outro sistema operacional (como Linux ou macOS), usa o comando "clear".
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
