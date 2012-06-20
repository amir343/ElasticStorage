package instance

import akka.actor.{ ActorLogging, ActorRef, Actor }
import cpu.OperationDuration
import protocol.{ KernelLoaded, KernelInit, KernelLog }
import akka.util.duration._

/**
 * Copyright 2012 Amir Moulavi (amir.moulavi@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Amir Moulavi
 */
class KernelActor extends Actor with ActorLogging {

  private var instance: ActorRef = _
  private var cpuSpeed: Long = _
  private var currentAccumulatedDelay: Long = 0
  private val sb = new StringBuilder

  def receive = {
    case KernelInit(speed) ⇒ initialize(speed)
  }

  private def initialize(speed: Long) {
    log.debug("Kernel initialized")
    instance = sender
    cpuSpeed = speed
    loadKernelInfo()
    loadFileSystem()
    bootUpKernel()
    startServices()
    kernelLoaded()
  }

  private def kernelLoaded() {
    val delay = OperationDuration.getExecutionOperation(cpuSpeed, 2000L)
    currentAccumulatedDelay += delay
    log.debug("Kernel loaded: " + currentAccumulatedDelay)
    context.system.scheduler.scheduleOnce(currentAccumulatedDelay milliseconds, instance, KernelLoaded())
  }

  private def loadKernelInfo() {
    schedule(1000L,
      sb.append("    Xen Minimal OS!\n")
        .append("     start_info: 0xa01000(VA)\n")
        .append("       nr_pages: 0x26700\n")
        .append("     shared_inf: 0xdee56000(MA)\n")
        .append("        pt_base: 0xa04000(VA)\n")
        .append("   nr_pt_frames: 0x9\n")
        .append("       mfn_list: 0x967000(VA)\n")
        .append("      mod_start: 0x0(VA)\n")
        .append("        mod_len: 0\n")
        .append("          flags: 0x0\n")
        .append("       cmd_line:  root=/dev/sda1 ro 4\n")
        .append("     stack:      0x946780-0x966780\n")
        .append("MM: Init\n")
        .append("       _text: 0x0(VA)\n")
        .append("      _etext: 0x621f5(VA)\n")
        .append("    _erodata: 0x76000(VA)\n")
        .append("      _edata: 0x7b6d4(VA)\n")
        .append(" stack start: 0x946780(VA)\n")
        .append("        _end: 0x966d34(VA)\n")
        .append("   start_pfn: a10")
        .append("     max_pfn: 26700").toString())
    schedule(2000L,
      sb.append("Mapping memory range 0xc00000 - 0x26700000\n")
        .append("setting 0x0-0x76000 readonly\n")
        .append("skipped 0x1000\n")
        .append("MM: Initialise page allocator for b3e000(b3e000)-0(26700000)\n")
        .append("MM: done\n")
        .append("Demand map pfns at 26701000-36701000.\n")
        .append("Heap resides at 36702000-76702000.\n")
        .append("Initialising timer interface\n")
        .append("Initialising console ... done.\n")
        .append("gnttab_table mapped at 0x26701000.\n")
        .append("Initialising scheduler\n")
        .append("Thread \"Idle\": pointer: 0x36702008, stack: 0xbf0000\n")
        .append("Initialising xenbus\n")
        .append("Thread \"xenstore\": pointer: 0x36702478, stack: 0x26600000\n")
        .append("Dummy main: start_info=0x966880\n")
        .append("Thread \"main\": pointer: 0x367028e8, stack: 0x26610000\n")
        .append("main\" \"root=/dev/sda1\" \"ro\" \"4\"\n")
        .append("vbd 2049 is hd0").toString())
  }

  private def loadFileSystem() {
    schedule(2000L,
      sb.append("root (hd0)\n")
        .append("\n")
        .append(" Filesystem type is ext3fs, using whole disk\n").toString())

    schedule(10000L,
      sb.append("\n")
        .append("kernel /boot/vmlinuz-2.6.35.11-83.9.SICS.i686 root=LABEL=/ console=hvc0\n").toString())

    schedule(10000L,
      sb.append("\n")
        .append("initrd /boot/initrd-2.6.35.11-83.9.SICS.i686.img\n").toString())

    schedule(10000L, "[Done]\n")
  }

  private def bootUpKernel() {
    schedule(1000L,
      sb.append(" Reserving virtual address space above 0xf5800000\n")
        .append(" Initializing cgroup subsys cpuset\n")
        .append(" Initializing cgroup subsys cpu\n")
        .append(" Linux version 2.6.35.11-83.9.SICS.i686 (mockbuild@build-31004.build) (gcc version 4.4.4 20100726 (Red Hat 4.4.4-13) (GCC) ) #1 SMP \n")
        .append(" ACPI in unprivileged domain disabled\n")
        .append(" released 0 pages of unused memory\n")
        .append(" BIOS-provided physical RAM map:\n")
        .append("  Xen: 0000000000000000 - 00000000000a0000 (usable)\n")
        .append("  Xen: 00000000000a0000 - 0000000000100000 (reserved)\n")
        .append("  Xen: 0000000000100000 - 0000000026700000 (usable)\n")
        .append(" NX (Execute Disable) protection: active\n")
        .append(" DMI not present or invalid.\n")
        .append(" last_pfn = 0x26700 max_arch_pfn = 0x1000000\n")
        .append(" init_memory_mapping: 0000000000000000-0000000026700000\n")
        .append(" RAMDISK: 01623000 - 0173b000\n")
        .append(" 0MB HIGHMEM available.\n")
        .append(" 615MB LOWMEM available.\n")
        .append("   mapped low ram: 0 - 26700000\n")
        .append("   low ram: 0 - 26700000\n")
        .append(" Zone PFN ranges:\n")
        .append("   DMA      0x00000001 -> 0x00001000\n")
        .append("   Normal   0x00001000 -> 0x00026700\n")
        .append("   HighMem  empty\n")
        .append(" Movable zone start PFN for each node\n")
        .append(" early_node_map[2] active PFN ranges\n")
        .append("     0: 0x00000001 -> 0x000000a0\n")
        .append("     0: 0x00000100 -> 0x00026700\n")
        .append(" Using APIC driver default\n")
        .append(" SMP: Allowing 1 CPUs, 0 hotplug CPUs\n")
        .append(" Local APIC disabled by BIOS -- you can enable it with \"lapic\"\n")
        .append(" APIC: disable apic facility\n")
        .append(" APIC: switched to apic NOOP\n")
        .append(" Allocating PCI resources starting at 26700000 (gap: 26700000:d9900000)\n")
        .append(" Booting paravirtualized kernel on Xen\n")
        .append(" Xen version: 3.1.2-128.1.10.el5 (preserve-AD)\n")
        .append(" setup_percpu: NR_CPUS:8 nr_cpumask_bits:8 nr_cpu_ids:1 nr_node_ids:1\n")
        .append(" PERCPU: Embedded 16 pages/cpu @c1503000 s42624 r0 d22912 u65536\n")
        .append(" pcpu-alloc: s42624 r0 d22912 u65536 alloc=16*4096\n")
        .append(" pcpu-alloc: [0] 0\n")
        .append(" Built 1 zonelists in Zone order, mobility grouping on.  Total pages: 156113\n")
        .append(" Kernel command line: root=LABEL=/ console=hvc0\n")
        .append(" PID hash table entries: 4096 (order: 2, 16384 bytes)\n")
        .append(" Dentry cache hash table entries: 131072 (order: 7, 524288 bytes)\n")
        .append(" Inode-cache hash table entries: 65536 (order: 6, 262144 bytes)\n")
        .append(" Enabling fast FPU save and restore... done.\n")
        .append(" Enabling unmasked SIMD FPU exception support... done.\n")
        .append(" Initializing CPU#0\n")
        .append(" Subtract (31 early reservations)\n")
        .append("   #1 [00017d8000 - 00017e8000]  XEN PAGETABLES\n")
        .append("   #2 [0000001000 - 0000002000]   EX TRAMPOLINE\n")
        .append("   #3 [0001000000 - 00014fcb94]   TEXT DATA BSS\n")
        .append("   #4 [0001623000 - 000173b000]         RAMDISK\n")
        .append("   #5 [000173b000 - 00017d8000]  XEN START INFO\n")
        .append("   #6 [0000002000 - 0000003000]      TRAMPOLINE\n")
        .append("   #7 [0000003000 - 0000007000]     ACPI WAKEUP\n")
        .append("   #8 [0000100000 - 0000222000]         PGTABLE\n")
        .append("   #9 [00014fd000 - 00014fe000]         BOOTMEM\n")
        .append("   #10 [00017e8000 - 0001cb8000]         BOOTMEM\n")
        .append("   #11 [00014fcbc0 - 00014fcbc4]         BOOTMEM\n")
        .append("   #12 [00014fcc00 - 00014fccc0]         BOOTMEM\n")
        .append("   #13 [00014fccc0 - 00014fcd34]         BOOTMEM\n")
        .append("   #14 [00014fe000 - 0001501000]         BOOTMEM\n")
        .append("   #15 [00014fcd40 - 00014fcdd0]         BOOTMEM\n")
        .append("   #16 [00014fce00 - 00014fce40]         BOOTMEM\n")
        .append("   #17 [00014fce40 - 00014fce80]         BOOTMEM\n")
        .append("   #18 [00014fce80 - 00014fcec0]         BOOTMEM\n")
        .append("   #19 [00014fcec0 - 00014fceda]         BOOTMEM\n")
        .append("   #20 [00014fcf00 - 00014fcf1a]         BOOTMEM\n")
        .append("   #21 [0001503000 - 0001513000]         BOOTMEM\n")
        .append("   #22 [00014fcf40 - 00014fcf44]         BOOTMEM\n")
        .append("   #23 [00014fcf80 - 00014fcf84]         BOOTMEM\n")
        .append("   #24 [00014fcfc0 - 00014fcfc4]         BOOTMEM\n")
        .append("   #25 [0001513000 - 0001513004]         BOOTMEM\n")
        .append("   #26 [0001513040 - 00015130c0]         BOOTMEM\n")
        .append("   #27 [00015130c0 - 00015130ec]         BOOTMEM\n")
        .append("   #28 [0001513100 - 0001517100]         BOOTMEM\n")
        .append("   #29 [0001517100 - 0001597100]         BOOTMEM\n")
        .append("   #30 [0001597100 - 00015d7100]         BOOTMEM\n").toString())

    schedule(2000L,
      sb.append(" 1 multicall(s) failed: cpu 0\n")
        .append(" Pid: 0, comm: swapper Not tainted 2.6.35.11-83.9.amzn1.i686 #1\n")
        .append(" SLUB: Genslabs=13, HWalign=64, Order=0-3, MinObjects=0, CPUs=1, Nodes=1\n")
        .append(" Hierarchical RCU implementation.\n")
        .append(" 	RCU dyntick-idle grace-period acceleration is enabled.\n")
        .append(" 	RCU-based detection of stalled CPUs is disabled.\n")
        .append(" 	Verbose stalled-CPUs detection is disabled.\n")
        .append(" NR_IRQS:2304 nr_irqs:256\n")
        .append(" Console: colour dummy device 80x25\n")
        .append(" console [tty0] enabled\n")
        .append(" console [hvc0] enabled\n")
        .append(" installing Xen timer for CPU 0\n")
        .append(" Detected 2659.994 MHz processor.\n")
        .append(" Calibrating delay loop (skipped), value calculated using timer frequency.. 5319.98 BogoMIPS (lpj=10639976)\n")
        .append(" pid_max: default: 32768 minimum: 301\n")
        .append(" Security Framework initialized\n")
        .append(" SELinux:  Disabled at boot.\n")
        .append(" Mount-cache hash table entries: 512\n")
        .append(" Initializing cgroup subsys ns\n")
        .append(" Initializing cgroup subsys cpuacct\n")
        .append(" Initializing cgroup subsys devices\n")
        .append(" Initializing cgroup subsys freezer\n")
        .append(" Performance Events: PEBS fmt0+, Core2 events, \n")
        .append(" no APIC, boot with the \"lapic\" boot parameter to force-enable it.\n")
        .append(" no hardware sampling interrupt available.\n")
        .append(" Intel PMU driver.\n")
        .append(" ... version:                2\n")
        .append(" ... bit width:              40\n")
        .append(" ... generic registers:      2\n")
        .append(" ... value mask:             000000ffffffffff\n")
        .append(" ... max period:             000000007fffffff\n")
        .append(" ... fixed-purpose events:   3\n")
        .append(" ... event mask:             0000000700000003\n")
        .append(" SMP alternatives: switching to UP code\n")
        .append("Freeing SMP alternatives: 16k freed\n").toString())

    schedule(1000L,
      sb.append(" cpu 0 spinlock event irq 1\n")
        .append("Brought up 1 CPUs\n")
        .append("devtmpfs: initialized\n")
        .append("Grant table initialized\n")
        .append("NET: Registered protocol family 16\n")
        .append("PCI: Fatal: No config space access function found\n")
        .append("bio: create slab <bio-0> at 0\n")
        .append("ACPI: Interpreter disabled.\n")
        .append("xen_balloon: Initialising balloon driver.\n")
        .append("vgaarb: loaded\n")
        .append(" Unpacking initramfs...\n")
        .append("Freeing initrd memory: 1120k freed\n")
        .append("platform rtc_cmos: registered platform RTC device (no PNP device found)\n")
        .append(" audit: initializing netlink socket (disabled)\n")
        .append("type=2000 audit(1301492688.874:1): initialized\n")
        .append("HugeTLB registered 2 MB page size, pre-allocated 0 pages\n")
        .append("VFS: Disk quotas dquot_6.5.2\n")
        .append("Dquot-cache hash table entries: 1024 (order 0, 4096 bytes)\n")
        .append("msgmni has been set to 1204\n")
        .append("alg: No test for stdrng (krng)\n")
        .append("Block layer SCSI generic (bsg) driver version 0.4 loaded (major 254)\n")
        .append("io scheduler noop registered (default)\n")
        .append("Serial: 8250/16550 driver, 4 ports, IRQ sharing disabled\n")
        .append("loop: module loaded\n")
        .append("blkfront device/vbd/2049 num-ring-pages 1 nr_ents 32.\n")
        .append("Initialising Xen virtual ethernet driver.\n")
        .append("blkfront: regular deviceid=0x801 major,minor=8,1, assuming parts/disk=16\n")
        .append("PNP: No PS/2 controller found. Probing ports directly.\n")
        .append("mice: PS/2 mouse device common for all mice\n")
        .append("cpuidle: using governor ladder\n")
        .append("cpuidle: using governor menu\n")
        .append("TCP cubic registered\n")
        .append("NET: Registered protocol family 17\n")
        .append("Using IPI No-Shortcut mode\n")
        .append("registered taskstats version 1\n")
        .append("Freeing unused kernel memory: 408k freed\n")
        .append("Write protecting the kernel text: 2760k\n").toString())

    schedule(2000L, "Write protecting the kernel read-only data: 1012k\n")
    schedule(2000L, "Mounting proc filesystem\n")
    schedule(2500L, "Mounting sysfs filesystem\n")
    schedule(2000L, "Creating /dev\n")
    schedule(2500L, "Creating initial device nodes\n")
    schedule(2000L, "Setting up hotplug.\n")
    schedule(1500L, "Creating block device nodes.\n")
    schedule(2000L, "Creating character device nodes.\n")
    schedule(1500L, "[Done]\n")
  }

  private def startServices() {
    schedule(1000L, "Starting system logger:\n")
    schedule(5000L,
      sb.append("[  OK  ]\n")
        .append("\n")
        .append("Setting hostname localhost.localdomain:\n").toString())
    schedule(2000L,
      sb.append("[  OK  ]\n")
        .append("\n")
        .append("Checking filesystems\n")
        .append("Checking all file systems.\n")
        .append("[/sbin/fsck.ext4 (1) -- /] fsck.ext4 -a /dev/xvda1\n").toString())
    schedule(12000L,
      sb.append("[  OK  ]\n")
        .append("\n")
        .append("Remounting root filesystem in read-write mode:  [   11.812895] EXT4-fs (xvda1): re-mounted. Opts: (null)\n").toString())
    schedule(2000L,
      sb.append("[  OK  ]\n")
        .append("")
        .append("Mounting local filesystems:\n").toString())
    schedule(3000L,
      sb.append("[  OK  ]\n")
        .append("\n")
        .append("Enabling local filesystem quotas:\n").toString())
    schedule(2000L,
      sb.append("[  OK  ]\n")
        .append("\n")
        .append("Enabling /etc/fstab swaps:\n").toString())
    schedule(2000L,
      sb.append("[  OK  ]\n")
        .append("\n")
        .append("Bringing up loopback interface:\n").toString())
    schedule(3000L,
      sb.append("[  OK  ]\n")
        .append("\n")
        .append("Bringing up interface eth0:\n").toString())
    schedule(2000L, "[  OK  ]\n")
  }

  def shutDown() {
    schedule(2000L,
      sb.append("\n")
        .append("*************************************\n")
        .append("Received SHUTDOWN Signal.").toString())
    schedule(1000L, "Preparing instance for SHUTDOWN...\n")
    schedule(2000L, "*************************************\n\n")
  }

  private def schedule(timeout: Long, log: String) {
    val delay = OperationDuration.getExecutionOperation(cpuSpeed, timeout)
    currentAccumulatedDelay += delay
    context.system.scheduler.scheduleOnce(currentAccumulatedDelay milliseconds, instance, KernelLog(log))
    sb.clear()
  }

}
