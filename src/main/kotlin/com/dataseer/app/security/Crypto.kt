package com.dataseer.app.security

import java.nio.ByteBuffer
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


/**
 * Static object that will be used to encrypt/decrypt [com.dataseer.app.model.DataSet] files
 * Maybe to be used for other stuff in the future?
 * Adapted from https://stackoverflow.com/a/30591380/9759267
 * @author Blendica Vlad
 * @date 24.03.2020
 */
object Crypto {

    private const val SEED_PREFIX = "jYtlhSJFmUQjMormx0G"

    /**
     * This method will encrypt the given data
     * @param key : the secret key that will be used to decrypt the data
     * @param data : the data that will be encrypted
     * @return Encrypted data as a byte array
     */
    @Throws(NoSuchPaddingException::class,
            NoSuchAlgorithmException::class,
            InvalidAlgorithmParameterException::class,
            InvalidKeyException::class,
            BadPaddingException::class,
            IllegalBlockSizeException::class,
            InvalidKeySpecException::class)
    fun encryptData(key: String, data: ByteArray?): ByteArray? {

        //Prepare the nonce
        val secureRandom = SecureRandom()

        //Nonce should be 12 bytes
        val iv = ByteArray(12)
        secureRandom.nextBytes(iv)

        //Prepare the key
        val secretKey = generateSecretKey(key, iv)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val parameterSpec = GCMParameterSpec(128, iv)

        //Encryption mode on
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec)

        //Encrypt the data
        val encryptedData = cipher.doFinal(data)

        //Concatenate everything and return the final data
        val byteBuffer = ByteBuffer.allocate(4 + iv.size + encryptedData.size)
        byteBuffer.putInt(iv.size)
        byteBuffer.put(iv)
        byteBuffer.put(encryptedData)
        return byteBuffer.array()
    }

    /**
     * This method will decrypt the given data
     * @param key [String] : the secret key that will be used to decrypt the data
     * @param encryptedData [String] : the data that will be encrypted
     * @return Decrypted data as a byte array
     */
    @Throws(NoSuchPaddingException::class,
            NoSuchAlgorithmException::class,
            InvalidAlgorithmParameterException::class,
            InvalidKeyException::class,
            BadPaddingException::class,
            IllegalBlockSizeException::class,
            InvalidKeySpecException::class)
    fun decryptData(key: String, encryptedData: ByteArray?): ByteArray? {

        //Wrap the data into a byte buffer to ease the reading process
        val byteBuffer = ByteBuffer.wrap(encryptedData)
        val noonceSize = byteBuffer.int

        //Make sure that the file was encrypted properly
        require(!(noonceSize < 12 || noonceSize >= 16)) {
            "Nonce size is incorrect. Make sure that the incoming data is an AES encrypted file."
        }
        val iv = ByteArray(noonceSize)
        byteBuffer[iv]

        //Prepare the key
        val secretKey = generateSecretKey(key, iv)

        //get the rest of encrypted data
        val cipherBytes = ByteArray(byteBuffer.remaining())
        byteBuffer[cipherBytes]
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val parameterSpec = GCMParameterSpec(128, iv)

        //Decryption mode on
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec)

        //Decrypt the data
        return cipher.doFinal(cipherBytes)
    }

    /**
     * Function to generate a 128 bit key from the given password and iv
     * @param seed
     * @param iv
     * @return Secret key
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    private fun generateSecretKey(seed: String, iv: ByteArray?): SecretKey {
        val spec: KeySpec = PBEKeySpec(SEED_PREFIX.plus(seed).toCharArray(), iv, 65536, 128) // AES-128
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val key = secretKeyFactory.generateSecret(spec).encoded
        return SecretKeySpec(key, "AES")
    }
}