package com.guy.sfpt.utils

import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.FileSystemOptions
import org.apache.commons.vfs2.Selectors
import org.apache.commons.vfs2.impl.StandardFileSystemManager
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder
import java.io.File

/**
 * Implement from java code
 * reference from https://stackoverflow.com/questions/21399561/sftp-upload-download-exist-and-move-using-apache-commons-vfs
 *
 * @author Ashok
 *
 */
object SFTPUtility {


    @JvmStatic
    fun main(args: Array<String>) {
//        val hostName = "52.230.85.199"
        val hostName = "192.168.1.105"
        val username = "Natthakit"
        val password = "espadano"
//        val localFilePath = "C:\\FakePath\\FakeFile.txt"
        val localFilePath = "src/main/resources/test.txt"
//        val remoteFilePath = "home/az-user/sftp/FakeRemoteFile.txt"
        val remoteFilePath = "C:/Users/Natthakit/Documents/test-windows.txt"
        val remoteTempFilePath = "FakeRemoteTempPath/FakeRemoteTempFile.txt"
        upload(hostName, username, password, localFilePath, remoteFilePath)
    }

    fun upload(hostName: String, username: String, password: String, localFilePath: String?, remoteFilePath: String) {
        val file = File(localFilePath)
        if (!file.exists()) throw RuntimeException("Error. Local file not found")
        val manager = StandardFileSystemManager()
        try {
            manager.init()

            val localFile: FileObject = manager.resolveFile(file.absolutePath)

            val remoteFile: FileObject = manager.resolveFile(createConnectionString(hostName, username, password, remoteFilePath), createDefaultOptions())

            remoteFile.copyFrom(localFile, Selectors.SELECT_SELF)
            println("File upload success")
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            manager.close()
        }
    }

    fun move(hostName: String, username: String, password: String, remoteSrcFilePath: String, remoteDestFilePath: String): Boolean {
        val manager = StandardFileSystemManager()
        return try {
            manager.init()

            val remoteFile: FileObject = manager.resolveFile(createConnectionString(hostName, username, password, remoteSrcFilePath), createDefaultOptions())
            val remoteDestFile: FileObject = manager.resolveFile(createConnectionString(hostName, username, password, remoteDestFilePath), createDefaultOptions())
            if (remoteFile.exists()) {
                remoteFile.moveTo(remoteDestFile)
                println("Move remote file success")
                true
            } else {
                println("Source file doesn't exist")
                false
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            manager.close()
        }
    }

    fun download(hostName: String, username: String, password: String, localFilePath: String?, remoteFilePath: String) {
        val manager = StandardFileSystemManager()
        try {
            manager.init()

            val localFile: FileObject = manager.resolveFile(localFilePath)

            val remoteFile: FileObject = manager.resolveFile(createConnectionString(hostName, username, password, remoteFilePath), createDefaultOptions())

            localFile.copyFrom(remoteFile, Selectors.SELECT_SELF)
            println("File download success")
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            manager.close()
        }
    }

    fun delete(hostName: String, username: String, password: String, remoteFilePath: String) {
        val manager = StandardFileSystemManager()
        try {
            manager.init()

            val remoteFile: FileObject = manager.resolveFile(createConnectionString(hostName, username, password, remoteFilePath), createDefaultOptions())
            if (remoteFile.exists()) {
                remoteFile.delete()
                println("Delete remote file success")
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            manager.close()
        }
    }

    fun exist(hostName: String, username: String, password: String, remoteFilePath: String): Boolean {
        val manager = StandardFileSystemManager()
        return try {
            manager.init()

            val remoteFile: FileObject = manager.resolveFile(createConnectionString(hostName, username, password, remoteFilePath), createDefaultOptions())
            System.out.println("File exist: " + remoteFile.exists())
            remoteFile.exists()
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            manager.close()
        }
    }

    fun createConnectionString(hostName: String, username: String, password: String, remoteFilePath: String): String {
        return "sftp://$username:$password@$hostName/$remoteFilePath"
    }

    @Throws(FileSystemException::class)
    fun createDefaultOptions(): FileSystemOptions {
        val opts = FileSystemOptions()

        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no")

        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false)

        SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000)
        return opts
    }
}