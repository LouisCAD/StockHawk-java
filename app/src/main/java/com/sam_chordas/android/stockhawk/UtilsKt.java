package com.sam_chordas.android.stockhawk;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;

import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

@Metadata(
   mv = {1, 1, 1},
   bv = {1, 0, 0},
   k = 2,
   d1 = {"\u0000\u0018\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a#\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0086\b¨\u0006\u0007"},
   d2 = {"runOnce", "", "Landroid/content/Context;", "uniqueKey", "", "f", "Lkotlin/Function0;", "production sources for module app"}
)
public final class UtilsKt {
   public static final void runOnce(@NotNull Context $receiver, @NotNull String uniqueKey, @NotNull Function0 f) {
      Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
      Intrinsics.checkParameterIsNotNull(uniqueKey, "uniqueKey");
      Intrinsics.checkParameterIsNotNull(f, "f");
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences($receiver);
      boolean isFirstRun = prefs.getBoolean(uniqueKey, true);
      if(isFirstRun) {
         prefs.edit().putBoolean(uniqueKey, false).commit();
         f.invoke();
      }

   }
}
