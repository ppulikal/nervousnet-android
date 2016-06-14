/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/prasad/Root/4_Development/Github/nervousnet-android/MobileClients/Android/nervousnet/nervousnetLIB/src/main/aidl/ch/ethz/coss/nervousnet/lib/NervousnetRemote.aidl
 */
package ch.ethz.coss.nervousnet.lib;
public interface NervousnetRemote extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements ch.ethz.coss.nervousnet.lib.NervousnetRemote
{
private static final java.lang.String DESCRIPTOR = "ch.ethz.coss.nervousnet.lib.NervousnetRemote";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an ch.ethz.coss.nervousnet.lib.NervousnetRemote interface,
 * generating a proxy if needed.
 */
public static ch.ethz.coss.nervousnet.lib.NervousnetRemote asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof ch.ethz.coss.nervousnet.lib.NervousnetRemote))) {
return ((ch.ethz.coss.nervousnet.lib.NervousnetRemote)iin);
}
return new ch.ethz.coss.nervousnet.lib.NervousnetRemote.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getReading:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
ch.ethz.coss.nervousnet.lib.SensorReading _result = this.getReading(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getReadings:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
long _arg1;
_arg1 = data.readLong();
long _arg2;
_arg2 = data.readLong();
java.util.List _arg3;
_arg3 = new java.util.ArrayList();
this.getReadings(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeList(_arg3);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements ch.ethz.coss.nervousnet.lib.NervousnetRemote
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/*
	 	* Returns latest Sensor values.
	    * sensorType = type of Sensor. Check LibConstants for types.
	    * startTime = from time , endTime = to time
	    * returns SensorReading object
	    */
@Override public ch.ethz.coss.nervousnet.lib.SensorReading getReading(int sensorType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
ch.ethz.coss.nervousnet.lib.SensorReading _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(sensorType);
mRemote.transact(Stub.TRANSACTION_getReading, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = ch.ethz.coss.nervousnet.lib.SensorReading.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*
	 	* Returns Sensor values in a List of SensorReading Objects.
	    * sensorType = type of Sensors. Check LibConstants for types.
	    * startTime = from time , endTime = to time
	    * list = list that will contain the returned objects of SensorReadings
	    */
@Override public void getReadings(int sensorType, long startTime, long endTime, java.util.List list) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(sensorType);
_data.writeLong(startTime);
_data.writeLong(endTime);
mRemote.transact(Stub.TRANSACTION_getReadings, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_reply.readList(list, cl);
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getReading = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getReadings = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
/*
	 	* Returns latest Sensor values.
	    * sensorType = type of Sensor. Check LibConstants for types.
	    * startTime = from time , endTime = to time
	    * returns SensorReading object
	    */
public ch.ethz.coss.nervousnet.lib.SensorReading getReading(int sensorType) throws android.os.RemoteException;
/*
	 	* Returns Sensor values in a List of SensorReading Objects.
	    * sensorType = type of Sensors. Check LibConstants for types.
	    * startTime = from time , endTime = to time
	    * list = list that will contain the returned objects of SensorReadings
	    */
public void getReadings(int sensorType, long startTime, long endTime, java.util.List list) throws android.os.RemoteException;
}
