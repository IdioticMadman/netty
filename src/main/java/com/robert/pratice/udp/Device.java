package com.robert.pratice.udp;


/**
 * Searcher搜集的到了provider的描述
 */
public class Device {

    private int port;
    private String sn;
    private String address;

    public Device(int port, String sn, String address) {
        this.port = port;
        this.sn = sn;
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Device{" +
                "port=" + port +
                ", sn='" + sn + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
