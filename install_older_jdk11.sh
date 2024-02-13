# Version JDK 11.0.12+ can be installed from the arch linux archive
yay -S downgrade
# Taken from https://archive.archlinux.org/repos/2021/09/01/extra/os/x86_64/
downgrade jre11-openjdk-headless=11.0.12.u7-1
downgrade jre11-openjdk=11.0.12.u7-1
downgrade jdk11-openjdk=11.0.12.u7-1

# All versions can be installed from Oracle's archive https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html
# The JDK11 archive download is broken, but you can download the .deb file and convert it to a compressed archive
yay -S deb2targz
