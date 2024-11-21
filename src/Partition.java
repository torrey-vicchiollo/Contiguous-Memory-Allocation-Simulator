
public class Partition {
    //the representation of each memory partition
    //base address
    private int base;
    //partition size
    private int length;
    //status: free or allocated
    private boolean bFree;
    //assigned process if allocated
    private String process;

    //constructor
    public Partition(int base, int length) {
        this.base = base;
        this.length = length;
        //free by default when creating
        this.bFree = true;
        //unallocated to any process
        this.process = null;
    }

    //get and set methods
    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isFree() {
        return bFree;
    }

    public void setFree(boolean bFree) {
        this.bFree = bFree;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String p) {
        this.process = p;
    }

    @Override
    public String toString() {
        return "Partition [base=" + base + ", length=" + length
                + ", bFree=" + bFree + ", process=" + process + "]";
    }
}
