package org.secfirst.umbrella.component

import android.app.Dialog
import android.content.Context
import android.view.View
import com.bluelinelabs.conductor.Controller
import java.util.*


class DialogManager(private val controller: Controller) {
    private var combos = HashSet<Combo>()

    companion object {
        const val PROGRESS_DIALOG_TAG = "MyProgressDialog"
    }

    init {
        controller.addLifecycleListener(object : Controller.LifecycleListener() {
            override fun postCreateView(controller: Controller, view: View) {
                for (combo in combos) {
                    combo.dialog = combo.factory.createDialog(controller.activity)
                    combo.dialog!!.show()
                }
            }

            override fun preDestroyView(controller: Controller, view: View) {
                val persistedCombos = HashSet<Combo>()

                for (combo in combos) {
                    if (combo.dialog!!.isShowing) {
                        combo.dialog!!.dismiss()
                        combo.dialog = null
                        persistedCombos.add(combo)
                    }
                }
                combos = persistedCombos
            }

            override fun preDestroy(controller: Controller) {
                combos.clear()
            }
        })
    }

    fun showDialog(factory: DialogFactory): Dialog {
        return showDialog(null, factory)
    }

    fun showDialog(tag: String?, factory: DialogFactory): Dialog {
        if (tag != null) {
            val found = findDialog(tag)
            if (found != null) {
                return found
            }
        }
        val dialog = factory.createDialog(controller.activity)

        dialog.show()
        combos.add(Combo(dialog, tag, factory))
        return dialog
    }

    private fun findDialog(tag: String?): Dialog? {
        if (tag == null) {
            throw NullPointerException("tag == null")
        }

        val combo = findCombo(tag)
        return combo?.dialog

    }

    private fun findCombo(tag: String): Combo? {
        for (combo in combos) {
            if (tag == combo.tag && combo.dialog != null && combo.dialog!!.isShowing) {
                return combo
            }
        }
        return null
    }

    internal class Combo(var dialog: Dialog?, val tag: String?, val factory: DialogFactory)

    interface DialogFactory {
        fun createDialog(context: Context?): Dialog
    }
}
