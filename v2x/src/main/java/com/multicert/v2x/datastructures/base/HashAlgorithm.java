package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COEREnumerationType;
import com.multicert.v2x.cryptography.Algorithm;
import com.multicert.v2x.cryptography.AlgorithmType;

public enum HashAlgorithm implements COEREnumerationType, AlgorithmType
{
    SHA_256;

    @Override
    public Algorithm getAlgorithm()
    {
        return new Algorithm(null,null,null,Algorithm.Hash.SHA_256);
    }
}
