package tk.rabenda.tenchi2mappermodifytool

import android.Manifest
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk15.coroutines.onClick
import ru.bartwell.exfilepicker.ExFilePicker
import ru.bartwell.exfilepicker.data.ExFilePickerResult
import java.io.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val EX_FILE_PICKER_RESULT = 0
        const val MY_PERMISSIONS_REQUEST_WRITE = 1
        const val tag = "MainActivity"
        private var fileName: String = "None"
        const val FILE_SIZE = 1024 * 1024 + 16
    }

    private fun setMapper(mapperValue: Int) {
        if (fileName == "None") {
            toast("请选择ROM")
        } else {
            val file = File(fileName)
            if(!file.exists()) {
                toast("文件不存在，请重新选择ROM")
            } else {
                val fileInputStream = FileInputStream(fileName)
                val buffer = ByteArray(FILE_SIZE)
                fileInputStream.read(buffer, 0, FILE_SIZE)
                fileInputStream.close()
                val mapperHighValue = mapperValue.and(0xF0)
                val mapperLowValue = mapperValue.and(0xF)
                Log.d(MainActivity.tag, buffer[6].toInt().and(0x0F).or(mapperLowValue.shl(4)).toString())
                buffer[6] = buffer[6].toInt().and(0x0F).or(mapperLowValue.shl(4)).toByte()
                Log.d(MainActivity.tag, buffer[7].toInt().and(0x0F).or(mapperHighValue).toString())
                buffer[7] = buffer[7].toInt().and(0x0F).or(mapperHighValue).toByte()
                val fileOutputStream = FileOutputStream(file)
                fileOutputStream.write(buffer)
                toast("设置完成 新mapper: " + mapperValue)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE),
            MY_PERMISSIONS_REQUEST_WRITE)

        verticalLayout {
            linearLayout {
                button("打开ROM") {
                    onClick {
                        val exFilePicker = ExFilePicker()
                        exFilePicker.setShowOnlyExtensions("nes")
                        exFilePicker.start(this@MainActivity, EX_FILE_PICKER_RESULT)
                    }
                }
            }
            linearLayout {
                button("Mapper4") {
                    onClick {
                        setMapper(4)
                    }
                }
                button("Mapper195") {
                    onClick {
                        setMapper(195)
                    }
                }
                button("Mapper198") {
                    onClick {
                        setMapper(198)
                    }
                }
                button("Mapper224") {
                    onClick {
                        setMapper(224)
                    }
                }
            }
            linearLayout {
                textView("设置Mapper为: ")
                val mapperEdit = editText() {
                    textSize = 26f
                   inputType = android.text.InputType.TYPE_CLASS_NUMBER
                }.lparams(width = dip(100))
                button("应用") {
                    onClick {
                        val mapperValue = mapperEdit.text.toString().toIntOrNull()
                        if (mapperValue != null) {
                            if (mapperValue < 0 || mapperValue > 255) {
                                toast("不合法的mapper iNes 1.0")
                            } else {
                                setMapper(mapperValue)
                            }
                        } else {
                            toast("不合法的Mapper，请重新输入")
                        }
                    }

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(MainActivity.tag, requestCode.toString())
        when (requestCode) {
            EX_FILE_PICKER_RESULT -> {
                val result = ExFilePickerResult.getFromIntent(data)
                if (result != null && result.count > 0) {
                    Log.d(MainActivity.tag, result.names.first())
                    fileName = result.path + result.names.first()
                    Log.d(MainActivity.tag, fileName)
                } else {
                    Log.d(MainActivity.tag,"OpenFileFailedResultNull")
                }
            }
            else -> {
                Log.d(MainActivity.tag,"OpenFileFailed")
            }
        }
    }
}
