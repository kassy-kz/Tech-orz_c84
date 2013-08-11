/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/kashimoto/Documents/workspace/MapIntentTest/src/orz/kassy/mapintenttest/ImainService.aidl
 */
package orz.kassy.mapintenttest;
public interface ImainService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements orz.kassy.mapintenttest.ImainService
{
private static final java.lang.String DESCRIPTOR = "orz.kassy.mapintenttest.ImainService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an orz.kassy.mapintenttest.ImainService interface,
 * generating a proxy if needed.
 */
public static orz.kassy.mapintenttest.ImainService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof orz.kassy.mapintenttest.ImainService))) {
return ((orz.kassy.mapintenttest.ImainService)iin);
}
return new orz.kassy.mapintenttest.ImainService.Stub.Proxy(obj);
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
case TRANSACTION_callFunction:
{
data.enforceInterface(DESCRIPTOR);
this.callFunction();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements orz.kassy.mapintenttest.ImainService
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
@Override public void callFunction() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_callFunction, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_callFunction = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void callFunction() throws android.os.RemoteException;
}
