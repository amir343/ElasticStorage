/**
 * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package instance.os;

import instance.cpu.OperationDuration;
import instance.gui.DummyInstanceGUI;
import instance.gui.InstanceGUI;
import logger.Logger;
import logger.LoggerFactory;

import java.util.Calendar;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-22
 *
 */

public class Kernel {

	private Logger logger;
	private long cpuSpeed;

    public Kernel(boolean headless) {
        if (!headless) logger = LoggerFactory.getLogger(Kernel.class, InstanceGUI.getInstance());
        else logger = LoggerFactory.getLogger(Kernel.class, new DummyInstanceGUI());
    }

	public void init(long cpuSpeed) {
		this.cpuSpeed = cpuSpeed;
		loadKernelInfo();
		loadFileSystemInfo();
		bootUpKernel();
		startServices();
	}

	private void startServices() {
		logger.raw("Starting system logger:"); 
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 5000L));
		logger.raw("[  OK  ]");
		logger.raw("");
		logger.raw("Setting hostname localhost.localdomain:");  
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 2000L));
		logger.raw("[  OK  ]");
		logger.raw("");
		logger.raw("Checking filesystems");
		logger.raw("Checking all file systems.");
		logger.raw("[/sbin/fsck.ext4 (1) -- /] fsck.ext4 -a /dev/xvda1");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 12000L));
		logger.raw("[  OK  ]");
		logger.raw("");
		logger.raw("Remounting root filesystem in read-write mode:  [   11.812895] EXT4-fs (xvda1): re-mounted. Opts: (null)");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 2000L));
		logger.raw("[  OK  ]");
		logger.raw("");
		logger.raw("Mounting local filesystems:");  
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 3000L));
		logger.raw("[  OK  ]");
		logger.raw("");
		logger.raw("Enabling local filesystem quotas:");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 2000L));
		logger.raw("[  OK  ]");
		logger.raw("");
		logger.raw("Enabling /etc/fstab swaps:");  
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 2000L));
		logger.raw("[  OK  ]");
		logger.raw("");
		logger.raw("Bringing up loopback interface:");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 3000L));
		logger.raw("[  OK  ]");
		logger.raw("");
		logger.raw("Bringing up interface eth0:");  
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 2000L));
		logger.raw("[  OK  ]");

		
	}

	private void bootUpKernel() {
		logger.raw(" Reserving virtual address space above 0xf5800000");
		logger.raw(" Initializing cgroup subsys cpuset");
		logger.raw(" Initializing cgroup subsys cpu");
		logger.raw(" Linux version 2.6.35.11-83.9.SICS.i686 (mockbuild@build-31004.build) (gcc version 4.4.4 20100726 (Red Hat 4.4.4-13) (GCC) ) #1 SMP " + Calendar.getInstance().getTime());
		logger.raw(" ACPI in unprivileged domain disabled");
		logger.raw(" released 0 pages of unused memory");
		logger.raw(" BIOS-provided physical RAM map:");
		logger.raw("  Xen: 0000000000000000 - 00000000000a0000 (usable)");
		logger.raw("  Xen: 00000000000a0000 - 0000000000100000 (reserved)");
		logger.raw("  Xen: 0000000000100000 - 0000000026700000 (usable)");
		logger.raw(" NX (Execute Disable) protection: active");
		logger.raw(" DMI not present or invalid.");
		logger.raw(" last_pfn = 0x26700 max_arch_pfn = 0x1000000");
		logger.raw(" init_memory_mapping: 0000000000000000-0000000026700000");
		logger.raw(" RAMDISK: 01623000 - 0173b000");
		logger.raw(" 0MB HIGHMEM available.");
		logger.raw(" 615MB LOWMEM available.");
		logger.raw("   mapped low ram: 0 - 26700000");
		logger.raw("   low ram: 0 - 26700000");
		logger.raw(" Zone PFN ranges:");
		logger.raw("   DMA      0x00000001 -> 0x00001000");
		logger.raw("   Normal   0x00001000 -> 0x00026700");
		logger.raw("   HighMem  empty");
		logger.raw(" Movable zone start PFN for each node");
		logger.raw(" early_node_map[2] active PFN ranges");
		logger.raw("     0: 0x00000001 -> 0x000000a0");
		logger.raw("     0: 0x00000100 -> 0x00026700");
		logger.raw(" Using APIC driver default");
		logger.raw(" SMP: Allowing 1 CPUs, 0 hotplug CPUs");
		logger.raw(" Local APIC disabled by BIOS -- you can enable it with \"lapic\"");
		logger.raw(" APIC: disable apic facility");
		logger.raw(" APIC: switched to apic NOOP");
		logger.raw(" Allocating PCI resources starting at 26700000 (gap: 26700000:d9900000)");
		logger.raw(" Booting paravirtualized kernel on Xen");
		logger.raw(" Xen version: 3.1.2-128.1.10.el5 (preserve-AD)");
		logger.raw(" setup_percpu: NR_CPUS:8 nr_cpumask_bits:8 nr_cpu_ids:1 nr_node_ids:1");
		logger.raw(" PERCPU: Embedded 16 pages/cpu @c1503000 s42624 r0 d22912 u65536");
		logger.raw(" pcpu-alloc: s42624 r0 d22912 u65536 alloc=16*4096");
		logger.raw(" pcpu-alloc: [0] 0"); 
		logger.raw(" Built 1 zonelists in Zone order, mobility grouping on.  Total pages: 156113");
		logger.raw(" Kernel command line: root=LABEL=/ console=hvc0");
		logger.raw(" PID hash table entries: 4096 (order: 2, 16384 bytes)");
		logger.raw(" Dentry cache hash table entries: 131072 (order: 7, 524288 bytes)");
		logger.raw(" Inode-cache hash table entries: 65536 (order: 6, 262144 bytes)");
		logger.raw(" Enabling fast FPU save and restore... done.");
		logger.raw(" Enabling unmasked SIMD FPU exception support... done.");
		logger.raw(" Initializing CPU#0");
		logger.raw(" Subtract (31 early reservations)");
		logger.raw("   #1 [00017d8000 - 00017e8000]  XEN PAGETABLES");
		logger.raw("   #2 [0000001000 - 0000002000]   EX TRAMPOLINE");
		logger.raw("   #3 [0001000000 - 00014fcb94]   TEXT DATA BSS");
		logger.raw("   #4 [0001623000 - 000173b000]         RAMDISK");
		logger.raw("   #5 [000173b000 - 00017d8000]  XEN START INFO");
		logger.raw("   #6 [0000002000 - 0000003000]      TRAMPOLINE");
		logger.raw("   #7 [0000003000 - 0000007000]     ACPI WAKEUP");
		logger.raw("   #8 [0000100000 - 0000222000]         PGTABLE");
		logger.raw("   #9 [00014fd000 - 00014fe000]         BOOTMEM");
		logger.raw("   #10 [00017e8000 - 0001cb8000]         BOOTMEM");
		logger.raw("   #11 [00014fcbc0 - 00014fcbc4]         BOOTMEM");
		logger.raw("   #12 [00014fcc00 - 00014fccc0]         BOOTMEM");
		logger.raw("   #13 [00014fccc0 - 00014fcd34]         BOOTMEM");
		logger.raw("   #14 [00014fe000 - 0001501000]         BOOTMEM");
		logger.raw("   #15 [00014fcd40 - 00014fcdd0]         BOOTMEM");
		logger.raw("   #16 [00014fce00 - 00014fce40]         BOOTMEM");
		logger.raw("   #17 [00014fce40 - 00014fce80]         BOOTMEM");
		logger.raw("   #18 [00014fce80 - 00014fcec0]         BOOTMEM");
		logger.raw("   #19 [00014fcec0 - 00014fceda]         BOOTMEM");
		logger.raw("   #20 [00014fcf00 - 00014fcf1a]         BOOTMEM");
		logger.raw("   #21 [0001503000 - 0001513000]         BOOTMEM");
		logger.raw("   #22 [00014fcf40 - 00014fcf44]         BOOTMEM");
		logger.raw("   #23 [00014fcf80 - 00014fcf84]         BOOTMEM");
		logger.raw("   #24 [00014fcfc0 - 00014fcfc4]         BOOTMEM");
		logger.raw("   #25 [0001513000 - 0001513004]         BOOTMEM");
		logger.raw("   #26 [0001513040 - 00015130c0]         BOOTMEM");
		logger.raw("   #27 [00015130c0 - 00015130ec]         BOOTMEM");
		logger.raw("   #28 [0001513100 - 0001517100]         BOOTMEM");
		logger.raw("   #29 [0001517100 - 0001597100]         BOOTMEM");
		logger.raw("   #30 [0001597100 - 00015d7100]         BOOTMEM");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 2000L));
		logger.raw(" 1 multicall(s) failed: cpu 0");
		logger.raw(" Pid: 0, comm: swapper Not tainted 2.6.35.11-83.9.amzn1.i686 #1");
		logger.raw(" SLUB: Genslabs=13, HWalign=64, Order=0-3, MinObjects=0, CPUs=1, Nodes=1");
		logger.raw(" Hierarchical RCU implementation.");
		logger.raw(" 	RCU dyntick-idle grace-period acceleration is enabled.");
		logger.raw(" 	RCU-based detection of stalled CPUs is disabled.");
		logger.raw(" 	Verbose stalled-CPUs detection is disabled.");
		logger.raw(" NR_IRQS:2304 nr_irqs:256");
		logger.raw(" Console: colour dummy device 80x25");
		logger.raw(" console [tty0] enabled");
		logger.raw(" console [hvc0] enabled");
		logger.raw(" installing Xen timer for CPU 0");
		logger.raw(" Detected 2659.994 MHz processor.");
		logger.raw(" Calibrating delay loop (skipped), value calculated using timer frequency.. 5319.98 BogoMIPS (lpj=10639976)");
		logger.raw(" pid_max: default: 32768 minimum: 301");
		logger.raw(" Security Framework initialized");
		logger.raw(" SELinux:  Disabled at boot.");
		logger.raw(" Mount-cache hash table entries: 512");
		logger.raw(" Initializing cgroup subsys ns");
		logger.raw(" Initializing cgroup subsys cpuacct");
		logger.raw(" Initializing cgroup subsys devices");
		logger.raw(" Initializing cgroup subsys freezer");
		logger.raw(" Performance Events: PEBS fmt0+, Core2 events, ");
		logger.raw(" no APIC, boot with the \"lapic\" boot parameter to force-enable it.");
		logger.raw(" no hardware sampling interrupt available.");
		logger.raw(" Intel PMU driver.");
		logger.raw(" ... version:                2");
		logger.raw(" ... bit width:              40");
		logger.raw(" ... generic registers:      2");
		logger.raw(" ... value mask:             000000ffffffffff");
		logger.raw(" ... max period:             000000007fffffff");
		logger.raw(" ... fixed-purpose events:   3");
		logger.raw(" ... event mask:             0000000700000003");
		logger.raw(" SMP alternatives: switching to UP code");
		logger.raw("Freeing SMP alternatives: 16k freed");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 1000L));
		logger.raw(" cpu 0 spinlock event irq 1");
		logger.raw("Brought up 1 CPUs");
		logger.raw("devtmpfs: initialized");
		logger.raw("Grant table initialized");
		logger.raw("NET: Registered protocol family 16");
		logger.raw("PCI: Fatal: No config space access function found");
		logger.raw("bio: create slab <bio-0> at 0");
		logger.raw("ACPI: Interpreter disabled.");
		logger.raw("xen_balloon: Initialising balloon driver.");
		logger.raw("vgaarb: loaded");
		logger.raw(" Unpacking initramfs...");
		logger.raw("Freeing initrd memory: 1120k freed");
		logger.raw("platform rtc_cmos: registered platform RTC device (no PNP device found)");
		logger.raw(" audit: initializing netlink socket (disabled)");
		logger.raw("type=2000 audit(1301492688.874:1): initialized");
		logger.raw("HugeTLB registered 2 MB page size, pre-allocated 0 pages");
		logger.raw("VFS: Disk quotas dquot_6.5.2");
		logger.raw("Dquot-cache hash table entries: 1024 (order 0, 4096 bytes)");
		logger.raw("msgmni has been set to 1204");
		logger.raw("alg: No test for stdrng (krng)");
		logger.raw("Block layer SCSI generic (bsg) driver version 0.4 loaded (major 254)");
		logger.raw("io scheduler noop registered (default)");
		logger.raw("Serial: 8250/16550 driver, 4 ports, IRQ sharing disabled");
		logger.raw("loop: module loaded");
		logger.raw("blkfront device/vbd/2049 num-ring-pages 1 nr_ents 32.");
		logger.raw("Initialising Xen virtual ethernet driver.");
		logger.raw("blkfront: regular deviceid=0x801 major,minor=8,1, assuming parts/disk=16");
		logger.raw("PNP: No PS/2 controller found. Probing ports directly.");
		logger.raw("mice: PS/2 mouse device common for all mice");
		logger.raw("cpuidle: using governor ladder");
		logger.raw("cpuidle: using governor menu");
		logger.raw("TCP cubic registered");
		logger.raw("NET: Registered protocol family 17");
		logger.raw("Using IPI No-Shortcut mode");
		logger.raw("registered taskstats version 1");
		logger.raw("Freeing unused kernel memory: 408k freed");
		logger.raw("Write protecting the kernel text: 2760k");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 2000L));
		logger.raw("Write protecting the kernel read-only data: 1012k");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 2000L));
		logger.raw("Mounting proc filesystem");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 2500L));
		logger.raw("Mounting sysfs filesystem");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 2000L));
		logger.raw("Creating /dev");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 2500L));
		logger.raw("Creating initial device nodes");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 2000L));
		logger.raw("Setting up hotplug.");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 1500L));
		logger.raw("Creating block device nodes.");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 2000L));
		logger.raw("Creating character device nodes.");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 1500L));
	}

	private void loadFileSystemInfo() {
		logger.raw("root (hd0)");
		logger.raw("");
		logger.raw(" Filesystem type is ext3fs, using whole disk");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 10000L));
		logger.raw("");
		logger.raw("kernel /boot/vmlinuz-2.6.35.11-83.9.SICS.i686 root=LABEL=/ console=hvc0");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 10000L));
		logger.raw("");
		logger.raw("initrd /boot/initrd-2.6.35.11-83.9.SICS.i686.img");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 10000L));		
	}

	private void loadKernelInfo() {
	    logger.raw("    Xen Minimal OS!");
   		logger.raw("     start_info: 0xa01000(VA)");
		logger.raw("       nr_pages: 0x26700");
		logger.raw("     shared_inf: 0xdee56000(MA)");
		logger.raw("        pt_base: 0xa04000(VA)");
		logger.raw("   nr_pt_frames: 0x9");
		logger.raw("       mfn_list: 0x967000(VA)");
		logger.raw("      mod_start: 0x0(VA)");
		logger.raw("        mod_len: 0");
		logger.raw("          flags: 0x0");
		logger.raw("       cmd_line:  root=/dev/sda1 ro 4");
		logger.raw("     stack:      0x946780-0x966780");
		logger.raw("MM: Init");
		logger.raw("       _text: 0x0(VA)");
		logger.raw("      _etext: 0x621f5(VA)");
		logger.raw("    _erodata: 0x76000(VA)");
		logger.raw("      _edata: 0x7b6d4(VA)");
		logger.raw(" stack start: 0x946780(VA)");
		logger.raw("        _end: 0x966d34(VA)");
		logger.raw("   start_pfn: a10");
		logger.raw("     max_pfn: 26700");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 2000L));
		logger.raw("Mapping memory range 0xc00000 - 0x26700000");
		logger.raw("setting 0x0-0x76000 readonly");
		logger.raw("skipped 0x1000");
		logger.raw("MM: Initialise page allocator for b3e000(b3e000)-0(26700000)");
		logger.raw("MM: done");
		logger.raw("Demand map pfns at 26701000-36701000.");
		logger.raw("Heap resides at 36702000-76702000.");
		logger.raw("Initialising timer interface");
		logger.raw("Initialising console ... done.");
		logger.raw("gnttab_table mapped at 0x26701000.");
		logger.raw("Initialising scheduler");
		logger.raw("Thread \"Idle\": pointer: 0x36702008, stack: 0xbf0000");
		logger.raw("Initialising xenbus");
		logger.raw("Thread \"xenstore\": pointer: 0x36702478, stack: 0x26600000");
		logger.raw("Dummy main: start_info=0x966880");
		logger.raw("Thread \"main\": pointer: 0x367028e8, stack: 0x26610000");
		logger.raw("\"main\" \"root=/dev/sda1\" \"ro\" \"4\""); 
		logger.raw("vbd 2049 is hd0");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 2000L));
	}
	
	private void sleep(long seconds) {
		try {
			Thread.sleep(seconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		logger.raw("");
		logger.raw("*************************************");
		logger.raw("Received SHUTDOWN Signal.");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 1000L));
		logger.raw("Preparing instance for SHUTDOWN...");
		sleep(OperationDuration.getExecutionOperation(cpuSpeed, 2000L));
		logger.raw("*************************************");
		logger.raw("");		
	}

}
