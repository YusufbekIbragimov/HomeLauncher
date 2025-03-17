package uz.yusuf.homelauncher

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import uz.yusuf.homelauncher.ui.theme.HomeLauncherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeLauncherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppGrid(
                        getInstalledApps(this),
                        this,
                        Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

fun isDefaultLauncher(context: Context): Boolean {
    val pm = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_HOME)
    }
    val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
    return resolveInfo?.activityInfo?.packageName == context.packageName
}

@SuppressLint("QueryPermissionsNeeded")
fun getInstalledApps(context: Context): List<ResolveInfo> {
    val pm = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
    return pm.queryIntentActivities(intent, 0)
}

@Composable
fun AppGrid(apps: List<ResolveInfo>, context: Context, modifier: Modifier) {
    LazyColumn {

        item {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                content = {
                    Text(
                        "Open Set Launcher Mode"
                    )
                },
                onClick = {
                    openHomeSettings(context)
                },
                enabled = true,
                shape = RoundedCornerShape(16.dp)
            )
        }

        items(apps) { app ->
            val pm = context.packageManager
            val appName = app.loadLabel(pm).toString()
            val icon = app.loadIcon(pm)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        val launchIntent =
                            pm.getLaunchIntentForPackage(app.activityInfo.packageName)
                        context.startActivity(launchIntent)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    bitmap = icon.toBitmap().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(appName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

fun openHomeSettings(context: Context) {
    val intent = Intent(Settings.ACTION_HOME_SETTINGS)
    context.startActivity(intent)
}