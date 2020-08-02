package app.obyte.client.compose

import app.obyte.client.protocol.Output

object OutputSorter: Comparator<Output> {
    override fun compare(a: Output, b: Output): Int {
        val compareByAddress = a.address.value.compareTo(b.address.value)

        if (compareByAddress == 0) {
            return a.amount.compareTo(b.amount)
        }
        return compareByAddress
    }
}
