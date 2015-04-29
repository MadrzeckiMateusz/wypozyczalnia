/**
 * 
 */
package pl.jeeweb.wypozyczalnia.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import pl.jeeweb.wypozyczalnia.entity.Filmy;

/**
 * @author Mateusz
 * 
 */
public class LazyFilmDataModel extends LazyDataModel<Filmy> {

	private List<Filmy> datasource;

	public LazyFilmDataModel(List<Filmy> datasource) {
		this.datasource = datasource;
	}

	@Override
	public Filmy getRowData(String rowKey) {
		for (Filmy film : datasource) {
			if (film.getId_filmu() == Integer.parseInt(rowKey))
				return film;
		}

		return null;
	}

	@Override
	public Integer getRowKey(Filmy film) {
		return new Integer(film.getId_filmu());
	}

	@Override
	public List<Filmy> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters) {
		List<Filmy> data = new ArrayList<Filmy>();

		// filter
		for (Filmy film : datasource) {
			boolean match = true;

			if (filters != null) {
				for (Iterator<String> it = filters.keySet().iterator(); it
						.hasNext();) {
					try {
						String filterProperty = it.next();
						Object filterValue = filters.get(filterProperty);
						String fieldValue = "";
						switch (filterProperty) {
						case "produkcja":
							fieldValue = film.getProdukcja();
							break;
						case "premiera":
							fieldValue = film.getPremiera().toString();
							break;
						case "gatunek_string":
							fieldValue = film.getGatunek_string();
							break;
						case "scenariusz":
							fieldValue = film.getScenariusz();
							break;
						case "rezyseria":
							fieldValue = film.getRezyseria();
							break;
						case "tytul":
							fieldValue = film.getTytul();
							break;
						default:
							break;
						}

						if (filterValue == null
								|| fieldValue.toLowerCase().contains(
										filterValue.toString().toLowerCase())) {
							match = true;
						} else {
							match = false;
							break;
						}
					} catch (Exception e) {
						match = false;
					}
				}
			}

			if (match) {
				data.add(film);
			}
		}

		

		// rowCount
		int dataSize = data.size();
		this.setRowCount(dataSize);

		// paginate
		if (dataSize > pageSize) {
			try {
				return data.subList(first, first + pageSize);
			} catch (IndexOutOfBoundsException e) {
				return data.subList(first, first + (dataSize % pageSize));
			}
		} else {
			return data;
		}
	}

}
