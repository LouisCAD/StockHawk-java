package com.sam_chordas.android.stockhawk.agera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.agera.BaseObservable;

import org.jetbrains.anko.Sdk15ServicesKt;
import org.jetbrains.annotations.NotNull;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(
   mv = {1, 1, 1},
   bv = {1, 0, 0},
   k = 1,
   d1 = {"\u00005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002*\u0001\r\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\b\u0010\u000f\u001a\u00020\u0010H\u0014J\b\u0010\u0011\u001a\u00020\u0010H\u0014R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\t\u001a\u00020\n8F¢\u0006\u0006\u001a\u0004\b\t\u0010\u000bR\u0010\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u000e¨\u0006\u0012"},
   d2 = {"Lcom/sam_chordas/android/stockhawk/agera/ConnectivityListener;", "Lcom/google/android/agera/BaseObservable;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "connectivityManager", "Landroid/net/ConnectivityManager;", "filter", "Landroid/content/IntentFilter;", "isNetworkConnected", "", "()Z", "receiver", "com/sam_chordas/android/stockhawk/agera/ConnectivityListener$receiver$1", "Lcom/sam_chordas/android/stockhawk/agera/ConnectivityListener$receiver$1;", "observableActivated", "", "observableDeactivated", "production sources for module app"}
)
public final class ConnectivityListener extends BaseObservable {
   private final ConnectivityManager connectivityManager;
   private final IntentFilter filter;
   private final BroadcastReceiver receiver;
   private final Context context;

   public final boolean isNetworkConnected() {
      NetworkInfo activeNetworkInfo = this.connectivityManager.getActiveNetworkInfo();
      return activeNetworkInfo != null && activeNetworkInfo.isConnected();
   }

   protected void observableActivated() {
      this.context.registerReceiver((BroadcastReceiver)this.receiver, this.filter);
   }

   protected void observableDeactivated() {
      this.context.unregisterReceiver((BroadcastReceiver)this.receiver);
   }

   public ConnectivityListener(@NotNull Context context) {
      super();
      Intrinsics.checkParameterIsNotNull(context, "context");
      this.context = context;
      this.connectivityManager = Sdk15ServicesKt.getConnectivityManager(this.context);
      this.filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
      this.receiver = new BroadcastReceiver() {
         public void onReceive(@NotNull Context context, @NotNull Intent intent) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(intent, "intent");
            ConnectivityListener.this.dispatchUpdate();
         }
      };
   }
}
