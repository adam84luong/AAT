package ch.bailu.aat_gtk.ui.view.solid

import ch.bailu.aat_gtk.ui.view.Label
import ch.bailu.aat_lib.preferences.OnPreferencesChanged
import ch.bailu.aat_lib.preferences.SolidIndexList
import ch.bailu.aat_lib.preferences.StorageInterface
import ch.bailu.gtk.GTK
import ch.bailu.gtk.gtk.Box
import ch.bailu.gtk.gtk.ComboBoxText
import ch.bailu.gtk.gtk.Orientation
import ch.bailu.gtk.type.Str


class SolidIndexComboView(private val solid: SolidIndexList) : OnPreferencesChanged {
    val layout = Box(Orientation.HORIZONTAL, 4)
    val label = Label()

    val combo = ComboBoxText()



    init {
        label.text = solid.label
        layout.packStart(label, GTK.FALSE, GTK.FALSE, 4)
        layout.packStart(combo, GTK.FALSE, GTK.FALSE, 4)

        val list = solid.stringArray

        for (index in 0 until solid.length()) {
            combo.insertText(index, Str(list[index]))
        }
        combo.active = solid.index

        combo.onChanged {
            solid.index = combo.active
        }
    }

    override fun onPreferencesChanged(storage: StorageInterface, key: String) {
        if (solid.hasKey(key)) {
            combo.active = solid.index
        }
    }
}