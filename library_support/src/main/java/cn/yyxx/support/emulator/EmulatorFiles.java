package cn.yyxx.support.emulator;

import java.io.File;

/**
 * @author #Suyghur.
 * Created on 2020/7/29
 */
public class EmulatorFiles {
    // =================== 主要证据 ===================
    /**
     * 特殊的模拟器特征文件
     * 186项文件，查到一个文件就是百分百模拟器，权重最高
     * <p>
     * 模拟器内核GoldFish
     * /data下的特征文件需要权限才可以访问，其他目录下的特征文件不需要
     * 因为模拟器的话，都是root权限，所以并不需要担心权限问题。
     * 转载请注明出处：CSDN 胖虎
     * https://blog.csdn.net/ljphhj/
     */
    static final String[] emulatorFiles = {

            // vbox模拟器文件
            "/data/youwave_id",
            "/dev/vboxguest",
            "/dev/vboxuser",
            "/mnt/prebundledapps/bluestacks.prop.orig",
            "/mnt/prebundledapps/propfiles/ics.bluestacks.prop.note",
            "/mnt/prebundledapps/propfiles/ics.bluestacks.prop.s2",
            "/mnt/prebundledapps/propfiles/ics.bluestacks.prop.s3",
            "/mnt/sdcard/bstfolder/InputMapper/com.bluestacks.appmart.cfg",
            "/mnt/sdcard/buildroid-gapps-ics-20120317-signed.tgz",
            "/mnt/sdcard/windows/InputMapper/com.bluestacks.appmart.cfg",
            "/proc/irq/9/vboxguest",
            "/sys/bus/pci/drivers/vboxguest",
            "/sys/bus/pci/drivers/vboxguest/0000:00:04.0",
            "/sys/bus/pci/drivers/vboxguest/bind",
            "/sys/bus/pci/drivers/vboxguest/module",
            "/sys/bus/pci/drivers/vboxguest/new_id",
            "/sys/bus/pci/drivers/vboxguest/remove_id",
            "/sys/bus/pci/drivers/vboxguest/uevent",
            "/sys/bus/pci/drivers/vboxguest/unbind",
            "/sys/bus/platform/drivers/qemu_pipe",
            "/sys/bus/platform/drivers/qemu_trace",
            "/sys/class/bdi/vboxsf-c",
            "/sys/class/misc/vboxguest",
            "/sys/class/misc/vboxuser",
            "/sys/devices/virtual/bdi/vboxsf-c",
            "/sys/devices/virtual/misc/vboxguest",
            "/sys/devices/virtual/misc/vboxguest/dev",
            "/sys/devices/virtual/misc/vboxguest/power",
            "/sys/devices/virtual/misc/vboxguest/subsystem",
            "/sys/devices/virtual/misc/vboxguest/uevent",
            "/sys/devices/virtual/misc/vboxuser",
            "/sys/devices/virtual/misc/vboxuser/dev",
            "/sys/devices/virtual/misc/vboxuser/power",
            "/sys/devices/virtual/misc/vboxuser/subsystem",
            "/sys/devices/virtual/misc/vboxuser/uevent",
            "/sys/module/vboxguest",
            "/sys/module/vboxguest/coresize",
            "/sys/module/vboxguest/drivers",
            "/sys/module/vboxguest/drivers/pci:vboxguest",
            "/sys/module/vboxguest/holders",
            "/sys/module/vboxguest/holders/vboxsf",
            "/sys/module/vboxguest/initsize",
            "/sys/module/vboxguest/initstate",
            "/sys/module/vboxguest/notes",
            "/sys/module/vboxguest/notes/.note.gnu.build-id",
            "/sys/module/vboxguest/parameters",
            "/sys/module/vboxguest/parameters/log",
            "/sys/module/vboxguest/parameters/log_dest",
            "/sys/module/vboxguest/parameters/log_flags",
            "/sys/module/vboxguest/refcnt",
            "/sys/module/vboxguest/sections",
            "/sys/module/vboxguest/sections/.altinstructions",
            "/sys/module/vboxguest/sections/.altinstr_replacement",
            "/sys/module/vboxguest/sections/.bss",
            "/sys/module/vboxguest/sections/.data",
            "/sys/module/vboxguest/sections/.devinit.data",
            "/sys/module/vboxguest/sections/.exit.text",
            "/sys/module/vboxguest/sections/.fixup",
            "/sys/module/vboxguest/sections/.gnu.linkonce.this_module",
            "/sys/module/vboxguest/sections/.init.text",
            "/sys/module/vboxguest/sections/.note.gnu.build-id",
            "/sys/module/vboxguest/sections/.rodata",
            "/sys/module/vboxguest/sections/.rodata.str1.1",
            "/sys/module/vboxguest/sections/.smp_locks",
            "/sys/module/vboxguest/sections/.strtab",
            "/sys/module/vboxguest/sections/.symtab",
            "/sys/module/vboxguest/sections/.text",
            "/sys/module/vboxguest/sections/__ex_table",
            "/sys/module/vboxguest/sections/__ksymtab",
            "/sys/module/vboxguest/sections/__ksymtab_strings",
            "/sys/module/vboxguest/sections/__param",
            "/sys/module/vboxguest/srcversion",
            "/sys/module/vboxguest/taint",
            "/sys/module/vboxguest/uevent",
            "/sys/module/vboxguest/version",
            "/sys/module/vboxsf",
            "/sys/module/vboxsf/coresize",
            "/sys/module/vboxsf/holders",
            "/sys/module/vboxsf/initsize",
            "/sys/module/vboxsf/initstate",
            "/sys/module/vboxsf/notes",
            "/sys/module/vboxsf/notes/.note.gnu.build-id",
            "/sys/module/vboxsf/refcnt",
            "/sys/module/vboxsf/sections",
            "/sys/module/vboxsf/sections/.bss",
            "/sys/module/vboxsf/sections/.data",
            "/sys/module/vboxsf/sections/.exit.text",
            "/sys/module/vboxsf/sections/.gnu.linkonce.this_module",
            "/sys/module/vboxsf/sections/.init.text",
            "/sys/module/vboxsf/sections/.note.gnu.build-id",
            "/sys/module/vboxsf/sections/.rodata",
            "/sys/module/vboxsf/sections/.rodata.str1.1",
            "/sys/module/vboxsf/sections/.smp_locks",
            "/sys/module/vboxsf/sections/.strtab",
            "/sys/module/vboxsf/sections/.symtab",
            "/sys/module/vboxsf/sections/.text",
            "/sys/module/vboxsf/sections/__bug_table",
            "/sys/module/vboxsf/sections/__param",
            "/sys/module/vboxsf/srcversion",
            "/sys/module/vboxsf/taint",
            "/sys/module/vboxsf/uevent",
            "/sys/module/vboxsf/version",
            "/sys/module/vboxvideo",
            "/sys/module/vboxvideo/coresize",
            "/sys/module/vboxvideo/holders",
            "/sys/module/vboxvideo/initsize",
            "/sys/module/vboxvideo/initstate",
            "/sys/module/vboxvideo/notes",
            "/sys/module/vboxvideo/notes/.note.gnu.build-id",
            "/sys/module/vboxvideo/refcnt",
            "/sys/module/vboxvideo/sections",
            "/sys/module/vboxvideo/sections/.data",
            "/sys/module/vboxvideo/sections/.exit.text",
            "/sys/module/vboxvideo/sections/.gnu.linkonce.this_module",
            "/sys/module/vboxvideo/sections/.init.text",
            "/sys/module/vboxvideo/sections/.note.gnu.build-id",
            "/sys/module/vboxvideo/sections/.rodata.str1.1",
            "/sys/module/vboxvideo/sections/.strtab",
            "/sys/module/vboxvideo/sections/.symtab",
            "/sys/module/vboxvideo/sections/.text",
            "/sys/module/vboxvideo/srcversion",
            "/sys/module/vboxvideo/taint",
            "/sys/module/vboxvideo/uevent",
            "/sys/module/vboxvideo/version",
            "/system/app/bluestacksHome.apk",
            "/system/bin/androVM-prop",
            "/system/bin/androVM-vbox-sf",
            "/system/bin/androVM_setprop",
            "/system/bin/get_androVM_host",
            "/system/bin/mount.vboxsf",
            "/system/etc/init.androVM.sh",
            "/system/etc/init.buildroid.sh",
            "/system/lib/hw/audio.primary.vbox86.so",
            "/system/lib/hw/camera.vbox86.so",
            "/system/lib/hw/gps.vbox86.so",
            "/system/lib/hw/gralloc.vbox86.so",
            "/system/lib/hw/sensors.vbox86.so",
            "/system/lib/modules/3.0.8-android-x86+/extra/vboxguest",
            "/system/lib/modules/3.0.8-android-x86+/extra/vboxguest/vboxguest.ko",
            "/system/lib/modules/3.0.8-android-x86+/extra/vboxsf",
            "/system/lib/modules/3.0.8-android-x86+/extra/vboxsf/vboxsf.ko",
            "/system/lib/vboxguest.ko",
            "/system/lib/vboxsf.ko",
            "/system/lib/vboxvideo.ko",
            "/system/usr/idc/androVM_Virtual_Input.idc",
            "/system/usr/keylayout/androVM_Virtual_Input.kl",

            "/system/xbin/mount.vboxsf",
            "/ueventd.android_x86.rc",
            "/ueventd.vbox86.rc",
            "/ueventd.goldfish.rc",
            "/fstab.vbox86",
            "/init.vbox86.rc",
            "/init.goldfish.rc",

            // ========针对原生Android模拟器 内核：goldfish===========
            "/sys/module/goldfish_audio",
            "/sys/module/goldfish_sync",

            // ========针对蓝叠模拟器===========
            "/data/app/com.bluestacks.appmart-1.apk",
            "/data/app/com.bluestacks.BstCommandProcessor-1.apk",
            "/data/app/com.bluestacks.help-1.apk",
            "/data/app/com.bluestacks.home-1.apk",
            "/data/app/com.bluestacks.s2p-1.apk",
            "/data/app/com.bluestacks.searchapp-1.apk",
            "/data/bluestacks.prop",
            "/data/data/com.androVM.vmconfig",
            "/data/data/com.bluestacks.accelerometerui",
            "/data/data/com.bluestacks.appfinder",
            "/data/data/com.bluestacks.appmart",
            "/data/data/com.bluestacks.appsettings",
            "/data/data/com.bluestacks.BstCommandProcessor",
            "/data/data/com.bluestacks.bstfolder",
            "/data/data/com.bluestacks.help",
            "/data/data/com.bluestacks.home",
            "/data/data/com.bluestacks.s2p",
            "/data/data/com.bluestacks.searchapp",
            "/data/data/com.bluestacks.settings",
            "/data/data/com.bluestacks.setup",
            "/data/data/com.bluestacks.spotlight",

            // ========针对逍遥安卓模拟器===========
            // 虚拟化网卡和pci，可能存在误判，不可靠
//            "/sys/module/virtio_net",
//            "/sys/module/virtio_pci",
            "/data/data/com.microvirt.download",
            "/data/data/com.microvirt.guide",
            "/data/data/com.microvirt.installer",
            "/data/data/com.microvirt.launcher",
            "/data/data/com.microvirt.market",
            "/data/data/com.microvirt.memuime",
            "/data/data/com.microvirt.tools",

            // ========针对Mumu模拟器===========
            "/data/data/com.mumu.launcher",
            "/data/data/com.mumu.store",
            "/data/data/com.netease.mumu.cloner"
    };


    public static boolean hasEmulatorFile() {
        for (String pipe : emulatorFiles) {
            File qemu_socket = new File(pipe);
            if (qemu_socket.exists()) {
                return true;
            }
        }
        return false;
    }
}
