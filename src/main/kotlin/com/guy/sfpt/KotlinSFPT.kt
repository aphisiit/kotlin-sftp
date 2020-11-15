@file:JvmName("KotlinSFPT")

package com.guy.sfpt

import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.FileSystemManager
import org.apache.commons.vfs2.Selectors
import org.apache.commons.vfs2.VFS
import java.io.IOException


private const val localFile = "src/main/resources/test.txt"

//private const val remoteDir = "C:/Users/Kritc/Downloads/"
//private const val username = "kritchanapak"
//private const val password = "Pt_11111111"
//private const val remoteHost = "172.20.10.2"
//private const val remoteHost = "ftp.example.com"

private const val remoteDir = "/home/az-user/sftp/"
private const val username = "az-user"
private const val password = "Pt_1111111111"
private const val remoteHost = "52.230.85.199"

private const val remoteFile = "Test.pdf"

//private const val localFile = "src/main/resources/test.txt"
//private const val remoteDir = "C:\\Users\\kritc\\Documents\\"


fun main(args: Array<String>) {
    uploadSFTP()
//    whenUploadFileUsingSshj_thenSuccess()
//    whenDownloadFileUsingSshj_thenSuccess()
}

fun uploadSFTP() {
    try {
        val manager: FileSystemManager = VFS.getManager()

        val local: FileObject = manager.resolveFile(
                System.getProperty("user.dir") + "/$localFile"
        )

        val remote: FileObject = manager.resolveFile(
                "sftp://$username:$password@$remoteHost/sftp/" + "test-remote.txt"
//                "sftp://$username:$password@$remoteHost/test-remote.txt"
        )

        remote.copyFrom(local, Selectors.SELECT_SELF)

        local.close()
        remote.close()
    }
    catch (e: Exception)
    {
        e.printStackTrace()
    }
}

fun downloadSFTP() {
    try {
        val manager: FileSystemManager = VFS.getManager()

        val local: FileObject = manager.resolveFile(
                System.getProperty("user.dir/$localFile")
        )

        val remote: FileObject = manager.resolveFile(
                "sftp://$username:$password@$remoteHost/$remoteDir/$remoteFile"
        )

        local.copyFrom(remote, Selectors.SELECT_SELF)

        local.close()
        remote.close()
    }
    catch (e: Exception)
    {
        e.printStackTrace()
    }
}

@Throws(IOException::class)
fun setupSshj(): SSHClient? {
    val client = SSHClient()
    client.addHostKeyVerifier(PromiscuousVerifier())
    client.connect(remoteHost)
    client.authPassword(username, password)
    return client
}

@Throws(IOException::class)
fun whenUploadFileUsingSshj_thenSuccess() {
    val sshClient = setupSshj()
    val sftpClient = sshClient!!.newSFTPClient()
    sftpClient.put(localFile, remoteDir + "test-remote.txt")
    sftpClient.close()
    sshClient.disconnect()
}

@Throws(IOException::class)
fun whenDownloadFileUsingSshj_thenSuccess() {
    val sshClient = setupSshj()
    val sftpClient = sshClient!!.newSFTPClient()
    sftpClient.get(remoteDir + remoteFile, "/Users/aphisitnamracha/Temp/Test.pdf")
    sftpClient.close()
    sshClient.disconnect()
}