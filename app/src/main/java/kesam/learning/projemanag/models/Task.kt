package kesam.learning.projemanag.models

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter.writeString

data class Task (
    var title: String = "",
    val createdBy: String = "",
    val cards: ArrayList<Card> = ArrayList()
): Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.createTypedArrayList(Card.CREATOR)!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int): Unit = with(dest) {
        this?.writeString(title)
        this?.writeString(createdBy)
        this?.writeTypedList(cards)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Task> = object : Parcelable.Creator<Task> {
            override fun createFromParcel(source: Parcel): Task = Task(source)
            override fun newArray(size: Int): Array<Task?> = arrayOfNulls(size)
        }
    }
}