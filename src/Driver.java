
import java.util.ArrayList;
import java.util.Scanner;

public class Driver {

    public static void main(String[] args) {
        //Choose an initial size of the main memory such as 2048, 4096, 8192, etc. (in terms of bytes)
        Scanner sc = new Scanner(System.in);
        System.out.print("TOTAL MEMORY SIZE[B] >> ");
        int memSize = sc.nextInt();
        int algorithm = -1;
        while (!(algorithm == 0 || algorithm == 1)) {
            System.out.print("\nALGORITHM [0=FIRST-FIT, 1=BEST-FIT) >> ");
            algorithm = sc.nextInt();
        }
        sc.close();
        ContigousMemoryAllocator mmu = new ContigousMemoryAllocator(memSize);
        //Generate a list of memory allocation requests of 100 processes with the format: Process ID = P + Number (e.g., P0, P1, …, P99), size = random number between 10 and 250.
        ArrayList<String> requests = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            String process = "P" + i;
            int size = 10 + (int) (Math.random() * 250);
            String request = process + "," + size;
            requests.add(request);
        }
        ArrayList<Double> successRatios = new ArrayList<>(), freeMemorySizes = new ArrayList<>();
        ArrayList<String> allocated = new ArrayList<>();
        Double totalAllocated = 0.0;
        //While (there are processes to allocate memory or unfinished execution) {
        while (!requests.isEmpty()) {
            System.out.print("\033[H\033[2J");
            Double totalSuccesses = 0.0, totalAttempts = 0.0;
            //Run the algorithm trying to allocate the memory for P0 to P99 as much as possible to fill into the main memory.
            for (int i = 0; i < requests.size(); i++) {
                String[] p = requests.get(i).split(",");
                String process = p[0];
                int size = Integer.parseInt(p[1]);
                if (algorithm == 0) {
                    if (mmu.first_fit(process, size) > -1) {
                        allocated.add(requests.get(i));
                        requests.remove(i);
                        totalSuccesses++;
                        totalAllocated++;
                    }
                } else {
                    if (mmu.best_fit(process, size) > -1) {
                        allocated.add(requests.get(i));
                        requests.remove(i);
                        totalSuccesses++;
                        totalAllocated++;
                    }
                }
                totalAttempts++;
            }
            successRatios.add(totalSuccesses / totalAttempts);
            System.out.println("-------------------------------------------------");
            //Print the list of all memory partitions after the allocation (sorted by base addresses).
            mmu.print_status();
            //Print the total free memory partition size.
            mmu.print_allocation();
            freeMemorySizes.add(mmu.free_memory() * 1.0);
            //Print the memory allocation ratio (#processes that have been allocated memory successfully per total processes)
            System.out.printf("MEMORY ALLOCATION RATIO >> %.2f\n", (totalAllocated / 100) * 1.0);
            //Sleep for 1 second (use Thread.sleep()), then choose one Process to mark it finished and release its allocated memory (choose among the ones that have been allocated memory).
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            int randomIndex = (int) (Math.random() * (allocated.size() - 1));
            String[] toRelease = allocated.get(randomIndex).split(",");
            mmu.release(toRelease[0]);
            allocated.remove(randomIndex);
        }
        System.out.println("-------------------------------------------------");
        //Print the average free memory size and average memory allocation success ratio (you need to count the #steps inside the while loop and do the running sum to compute the average at this step).
        Double averageFreeMemorySize = 0.0;
        for (Double freeMemorySize : freeMemorySizes) {
            averageFreeMemorySize += freeMemorySize;
        }
        averageFreeMemorySize = averageFreeMemorySize / freeMemorySizes.size();
        System.out.printf("AVG. FREE MEMORY SIZE >> %f\n", averageFreeMemorySize);
        Double averageSucessRatio = 0.0;
        for (Double successRatio : successRatios) {
            averageSucessRatio += successRatio;
        }
        averageSucessRatio = averageSucessRatio / successRatios.size();
        System.out.printf("AVG. MEMORY ALLOCATION SUCCESS RATIO >> %f\n", averageSucessRatio);
    }
}
