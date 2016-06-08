package com.aau.bluetooth;



public interface IInstrumentationProvider {
    public void addInstrumentationListener(IInstrumentationListener listener);
    public void removeInstrumentationListener(IInstrumentationListener listener);
}

