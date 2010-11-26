package com.ladoservidor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Guestbook extends WebPage {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private Map<Date, String> guestbook = new TreeMap<Date, String>();

	private String mensagem = "";

	@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
	public Guestbook() {
		Form form = new Form("form") {
			protected void onSubmit() {
				guestbook.put(Calendar.getInstance().getTime(), mensagem);
				mensagem = "";

				logger.info("Mensagem adicionada. Total é [{}]", guestbook.size());
			}
		};

		form.add(new FeedbackPanel("validacoes"));
		form.setModel(new CompoundPropertyModel(this));

		form.add(new RequiredTextField("mensagem"));
		add(form);

		LoadableDetachableModel modelRecados = new LoadableDetachableModel() {
			protected Object load() {
				return new ArrayList(guestbook.entrySet());
			}
		};

		WebMarkupContainer ajaxpage = new WebMarkupContainer("ajaxpage");
		ajaxpage.setOutputMarkupId(true);
		
		add(ajaxpage);
		

		int itensPorPagina = 3;
		PageableListView recados = new PageableListView("recados", modelRecados, itensPorPagina) {
			protected void populateItem(ListItem item) {
				Entry<Date, String> entrada = (Entry<Date, String>) item.getModelObject();
				final Date data = entrada.getKey();
				String recado = entrada.getValue();

				item.add(new Label("recado", recado));
				item.add(new Label("data", data.toString()));
				
				item.add(new Link("apagar") {
					public void onClick() {
						guestbook.remove(data);
					}
				});
			}
		};

		ajaxpage.add(recados);
		
		ajaxpage.add(new AjaxPagingNavigator("paginacao", recados));
	}

}
