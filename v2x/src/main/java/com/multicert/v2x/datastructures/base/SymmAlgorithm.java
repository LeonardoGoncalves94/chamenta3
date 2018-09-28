package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COEREnumerationType;
import com.multicert.v2x.cryptography.Algorithm;
import com.multicert.v2x.cryptography.AlgorithmType;

/**
 * This enumeration indicates the supported symmetric algorithms
 */
public enum SymmAlgorithm implements COEREnumerationType, AlgorithmType
{
    AES_128_CCM;

    @Override
    public Algorithm getAlgorithm()
    {
        return new Algorithm(Algorithm.Symmetric.AES_128_CCM, null, null, Algorithm.Hash.SHA_256);
    }
}
