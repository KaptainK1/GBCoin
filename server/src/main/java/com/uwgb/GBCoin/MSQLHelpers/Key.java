package com.uwgb.GBCoin.MSQLHelpers;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;

import java.security.PublicKey;

public class Key extends AbstractSingleColumnStandardBasicType<java.security.Key> {
    public Key() {
        super(BlobTypeDescriptor.DEFAULT, new KeyDescriptor());
    }

    @Override
    public String getName() {
        return "Key";
    }

    @Override
    public Object resolve(Object value, SharedSessionContractImplementor session, Object owner, Boolean overridingEager) throws HibernateException {
        return null;
    }

}
