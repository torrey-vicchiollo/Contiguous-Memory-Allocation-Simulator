
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContigousMemoryAllocator {

    //maximum memory size in bytes (B)
    private int size;
    //map process to partition
    private Map<String, Partition> allocMap;
    //list of memory partitions
    private List<Partition> partList;

    //constructor
    public ContigousMemoryAllocator(int size) {
        this.size = size;
        this.allocMap = new HashMap<>();
        this.partList = new ArrayList<>();
        //add the first hole, which is the whole memory at start-up
        this.partList.add(new Partition(0, size));
    }

    //prints the allocation map in ascending order of base addresses
    public void print_status() {
        for (Partition part : partList) {
            System.out.printf("[%d-%d] %s\n",
                    part.getBase(), part.getBase() + part.getLength() - 1, part.isFree() ? "FREE" : part.getProcess() + "[" + part.getLength() + "B]");
        }
    }

    public void print_allocation() {
        System.out.printf("ALLOCATED >> %d FREE >> %d\n", allocated_memory(), free_memory());
    }

    //get the total allocated memory size
    private int allocated_memory() {
        int allocated = 0;
        for (Partition part : partList) {
            if (!part.isFree()) {
                allocated += part.getLength();
            }
        }
        return allocated;
    }

    //get the total free memory size
    public int free_memory() {
        int free = 0;
        for (Partition part : partList) {
            if (part.isFree()) {
                free += part.getLength();
            }
        }
        return free;
    }

    //sort the list of partitions in ascending order of base addresses
    private void order_partitions() {
        Collections.sort(partList, Comparator.comparingInt(Partition::getBase));
    }

    //implements the first fit memory allocation algorithm
    public int first_fit(String process, int size) {
        //make sure no partition allocated to process before
        if (allocMap.containsKey(process)) {
            //duplicate request from the process
            return -1;
        }
        //try to find the first big enough free partition to allocate to the process
        int alloc = -1, index = 0;
        while (index < partList.size()) {
            Partition part = partList.get(index);
            //first big enough free partition
            if (part.isFree() && part.getLength() >= size) {
                Partition p = new Partition(part.getBase(), size);
                p.setProcess(process);
                p.setFree(false);
                //add the allocated partition to the list
                partList.add(index, p);
                //put the new mapping to prevent duplication later
                allocMap.put(process, p);
                //update the current free partition
                part.setBase(part.getBase() + size);
                part.setLength(part.getLength() - size);
                if (part.getLength() == 0) {
                    partList.remove(part);
                }
                alloc = size;
                break;
            } else {
                //try with the next partition
                index++;
            }
        }
        //positive -> allocate successfully, negative -> error or insufficient
        return alloc;
    }

    //implements the best-fit memory allocation algorithm
    public int best_fit(String process, int size) {
        if (allocMap.containsKey(process)) {
            return -1;
        }
        int alloc = -1, index = 0, bestindex = -1;
        while (index < partList.size()) {
            if (partList.get(index).isFree() && partList.get(index).getLength() >= size) {
                if (bestindex == -1) {
                    bestindex = index;
                    alloc = size;
                } else if (partList.get(bestindex).getLength() > partList.get(index).getLength()) {
                    bestindex = index;
                    alloc = size;
                }
            }
            index++;
        }
        if (bestindex != -1) {
            Partition part = partList.get(bestindex);
            Partition p = new Partition(part.getBase(), size);
            p.setProcess(process);
            p.setFree(false);
            partList.add(index, p);
            allocMap.put(process, p);
            part.setBase(part.getBase() + size);
            part.setLength(part.getLength() - size);
            if (part.getLength() == 0) {
                partList.remove(part);
            }
        }
        order_partitions();
        return alloc;
    }

    //release the allocated memory of a process
    public int release(String process) {
        int free = -1;
        for (Partition part : partList) {
            if (!part.isFree() && part.getProcess().equals(process)) {
                //true -> free memory
                part.setFree(true);
                part.setProcess("");
                free = part.getLength();
                break;
            }
        }
        if (free < 0) {
            //failure to release -> not found process or wrong information
            return free;
        }
        //merge adjacent free memory partitions into bigger ones
        merge_holes();
        //success in releasing process memory
        return free;
    }

    private void merge_holes() {
        //sort the partitions in partList in ascending order of base addressess
        order_partitions();
        //index of the first partition
        int i = 0;
        while (i < partList.size()) {
            Partition part = partList.get(i);
            //free partition -> try to merge with the next free neighbor
            if (part.isFree()) {
                //another loop to merge with adjacent free neighbor partitions
                //right next neighbor
                int j = i + 1;
                while (j < partList.size() && partList.get(j).isFree()) {
                    if (partList.get(j).getBase() == part.getBase() + part.getLength()) {
                        //safe to merge them
                        part.setLength(part.getLength() + partList.get(j).getLength());
                        partList.remove(j);
                    } else {
                        //cannot merge -> stop
                        break;
                    }
                }
                //not free -> try with the next partition
                i++;
            } else {
                //not free -> try with the next partition
                i++;
            }
        }
    }
}
