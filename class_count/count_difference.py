for jdk in ["jdk11", "jdk11.0.11", "jdk11.0.12", "jdk11.0.21"]:
    lin = set()
    with open(f"{jdk}.linux.txt", "r") as f:
        for line in f:
            line = line.strip()
            if line.endswith(".class"):
                lin.add(line.strip())

    win = set()
    with open(f"{jdk}.windows.txt", "r") as f:
        for line in f:
            line = line.strip()
            if line.endswith(".class"):
                win.add(line.strip())
    
    print(jdk)
    print("Windows")
    print(len(win))
    print("Linux")
    print(len(lin))
    print("Intersection")
    print(len(lin&win))

