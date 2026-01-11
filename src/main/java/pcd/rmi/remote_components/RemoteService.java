package pcd.rmi.remote_components;

import pcd.rmi.PixelGrid;
import pcd.rmi.RemoteEvent;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteService extends Remote {
    void handleEvent(RemoteEvent event) throws RemoteException;
    Integer join(RemoteServiceListener rsl) throws RemoteException;

    void setGrid(PixelGrid grid) throws RemoteException;

    PixelGrid getGrid() throws RemoteException;
}

