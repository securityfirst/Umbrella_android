package org.secfirst.umbrella.data.disk

import org.eclipse.jgit.lib.BatchingProgressMonitor
import java.io.IOException
import java.io.Writer

abstract class TentMonitor(private val out: Writer) : BatchingProgressMonitor() {


    private var write: Boolean = false

    init {
        this.write = true
    }

    override fun onUpdate(taskName: String, workCurr: Int) {
        val s = StringBuilder()
        format(s, taskName, workCurr)
        send(s)
    }

    override fun onEndTask(taskName: String, workCurr: Int) {
        val s = StringBuilder()
        format(s, taskName, workCurr)
        s.append("\n") //$NON-NLS-1$
        send(s)
    }

    private fun format(s: StringBuilder, taskName: String, workCurr: Int) {
        s.append("\r") //$NON-NLS-1$
        s.append(taskName)
        s.append(": ") //$NON-NLS-1$
        while (s.length < 25)
            s.append(' ')
        s.append(workCurr)
    }

    override fun onEndTask(taskName: String, cmp: Int, totalWork: Int, pcnt: Int) {
        val s = StringBuilder()
        format(s, taskName, cmp, totalWork, pcnt)
        s.append("\n") //$NON-NLS-1$
        send(s)
    }

    private fun format(
        s: StringBuilder, taskName: String, cmp: Int,
        totalWork: Int, pcnt: Int
    ) {
        s.append("\r") //$NON-NLS-1$
        s.append(taskName)
        s.append(": ") //$NON-NLS-1$
        while (s.length < 25)
            s.append(' ')

        val endStr = totalWork.toString()
        var curStr = cmp.toString()
        while (curStr.length < endStr.length)
            curStr = " $curStr" //$NON-NLS-1$
        if (pcnt < 100)
            s.append(' ')
        if (pcnt < 10)
            s.append(' ')
        s.append(pcnt)
        s.append("% (") //$NON-NLS-1$
        s.append(curStr)
        s.append("/") //$NON-NLS-1$
        s.append(endStr)
        s.append(")") //$NON-NLS-1$
    }

    private fun send(s: StringBuilder) {
        if (write) {
            try {
                out.write(s.toString())
                out.flush()
            } catch (err: IOException) {
                write = false
            }

        }
    }
}