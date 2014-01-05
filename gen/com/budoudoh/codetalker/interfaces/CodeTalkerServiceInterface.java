/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/basilu/Documents/workspace/CodeTalker/src/com/budoudoh/codetalker/interfaces/CodeTalkerServiceInterface.aidl
 */
package com.budoudoh.codetalker.interfaces;
public interface CodeTalkerServiceInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.budoudoh.codetalker.interfaces.CodeTalkerServiceInterface
{
private static final java.lang.String DESCRIPTOR = "com.budoudoh.codetalker.interfaces.CodeTalkerServiceInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.budoudoh.codetalker.interfaces.CodeTalkerServiceInterface interface,
 * generating a proxy if needed.
 */
public static com.budoudoh.codetalker.interfaces.CodeTalkerServiceInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.budoudoh.codetalker.interfaces.CodeTalkerServiceInterface))) {
return ((com.budoudoh.codetalker.interfaces.CodeTalkerServiceInterface)iin);
}
return new com.budoudoh.codetalker.interfaces.CodeTalkerServiceInterface.Stub.Proxy(obj);
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
case TRANSACTION_refreshProfileCache:
{
data.enforceInterface(DESCRIPTOR);
this.refreshProfileCache();
reply.writeNoException();
return true;
}
case TRANSACTION_loadProfileCache:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.loadProfileCache();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_fetchCategories:
{
data.enforceInterface(DESCRIPTOR);
this.fetchCategories();
reply.writeNoException();
return true;
}
case TRANSACTION_refreshPurchasedProfiles:
{
data.enforceInterface(DESCRIPTOR);
this.refreshPurchasedProfiles();
reply.writeNoException();
return true;
}
case TRANSACTION_refreshUser:
{
data.enforceInterface(DESCRIPTOR);
this.refreshUser();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.budoudoh.codetalker.interfaces.CodeTalkerServiceInterface
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
@Override public void refreshProfileCache() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_refreshProfileCache, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean loadProfileCache() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_loadProfileCache, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void fetchCategories() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_fetchCategories, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void refreshPurchasedProfiles() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_refreshPurchasedProfiles, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void refreshUser() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_refreshUser, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_refreshProfileCache = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_loadProfileCache = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_fetchCategories = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_refreshPurchasedProfiles = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_refreshUser = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public void refreshProfileCache() throws android.os.RemoteException;
public boolean loadProfileCache() throws android.os.RemoteException;
public void fetchCategories() throws android.os.RemoteException;
public void refreshPurchasedProfiles() throws android.os.RemoteException;
public void refreshUser() throws android.os.RemoteException;
}
