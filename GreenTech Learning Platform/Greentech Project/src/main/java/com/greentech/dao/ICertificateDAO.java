package com.greentech.dao;

import com.greentech.exception.DatabaseException;
import com.greentech.model.Certificate;

import java.util.List;

/**
 * Contract for Course Certificate generation, storage, and validation.
 */
public interface ICertificateDAO extends IGenericDAO<Certificate, Integer> {
    Certificate issueCertificate(int learnerId, int courseId) throws DatabaseException;
    List<Certificate> getCertificatesByLearner(int learnerId) throws DatabaseException;
    Certificate getByCode(String code) throws DatabaseException;
}
