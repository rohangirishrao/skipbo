package skipbo.server;

/**
 * Exception thrown when a user want to take a name that is already in use.
 */
public class NameTakenException extends Exception {
    String name = null;
    SBListener sbL = null;

    public NameTakenException(String name, SBListener sbL) {
        this.name = name;
        this.sbL = sbL;
    }

    /**
     * @return a name that is not in use yet, according to the name chosen in the first place.
     */
    public String findName() {
        int i = 1;
        String nameWithNumber = this.name + i;
        while (sbL.getServer().getLobby().nameIsTaken(nameWithNumber)) {
            i++;
            nameWithNumber = name + i;
        }
        this.sbL.getPW().println(Protocol.PRINT + "§Terminal§" + name + " was taken. Your name was set to " + nameWithNumber + ".");
        return nameWithNumber;
    }
}
