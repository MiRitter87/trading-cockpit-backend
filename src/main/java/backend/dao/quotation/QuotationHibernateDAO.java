package backend.dao.quotation;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import backend.model.StockExchange;
import backend.model.instrument.Quotation;

/**
 * Provides access to Quotation database persistence using Hibernate.
 * 
 * @author Michael
 */
public class QuotationHibernateDAO implements QuotationDAO {
	/**
	 * Factory for database session.
	 */
	protected EntityManagerFactory sessionFactory;

	
	/**
	 * Default constructor.
	 * 
	 * @param sessionFactory The database session factory.
	 */
	public QuotationHibernateDAO(final EntityManagerFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	

	@Override
	public Quotation getCurrentQuotation(String symbol, StockExchange stockExchange) throws Exception {
		throw new Exception("Operation not supported.");
	}

	@Override
	public List<Quotation> getQuotationHistory(String symbol, StockExchange stockExchange, Integer years) throws Exception {
		throw new Exception("Operation not supported.");
	}

	@Override
	public void insertQuotations(List<Quotation> quotations) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteQuotations(List<Quotation> quotations) throws Exception {
		// TODO Auto-generated method stub

	}

}
