package com.bse.daisybuzz.helper;

import com.bse.daizybuzz.model.Cadeau;
import com.bse.daizybuzz.model.Categorie;
import com.bse.daizybuzz.model.Localisation;
import com.bse.daizybuzz.model.Marque;
import com.bse.daizybuzz.model.MarqueCategorie;
import com.bse.daizybuzz.model.Pdv;
import com.bse.daizybuzz.model.PdvPoi;
import com.bse.daizybuzz.model.Poi;
import com.bse.daizybuzz.model.Produit;
import com.bse.daizybuzz.model.Questionnaire;
import com.bse.daizybuzz.model.RaisonAchat;
import com.bse.daizybuzz.model.RaisonRefus;
import com.bse.daizybuzz.model.Rapport;
import com.bse.daizybuzz.model.Superviseur;
import com.bse.daizybuzz.model.TrancheAge;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteDatabaseHelper extends SQLiteOpenHelper {

	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 10;

	// Database Name
	private static final String DATABASE_NAME = "contactsManager";

	// Table Names
	private static final String TABLE_MARQUE = "marque";
	private static final String TABLE_PRODUIT = "produit";
	private static final String TABLE_CATEGORIE = "categorie";
	private static final String TABLE_MARQUE_CATEGORIE = "marque_categorie";
	private static final String TABLE_PDV = "pdv";;
	private static final String TABLE_CADEAU = "cadeau";
	private static final String TABLE_SUPERVISEUR = "superviseur";
	private static final String TABLE_RAISONACHAT = "raisonachat";
	private static final String TABLE_RAISONREFUS = "raisonrefus";
	private static final String TABLE_TRANCHEAGE = "trancheage";
	private static final String TABLE_LOCALISATION = "localisation";
	private static final String TABLE_RAPPORT = "rapport";
	private static final String TABLE_POI = "poi";
	private static final String TABLE_PDV_POI = "pdv_poi";
	
	private static final String TABLE_QUESTIONNAIRE = "questionnaire";

	// Common column names
	private static final String KEY_ID = "id";
	private static final String KEY_NOM = "nom";
	private static final String KEY_PRENOM = "prenom";
	private static final String KEY_DATE_CREATION = "dateCreation";

	// MARQUE Table - column names
	private static final String KEY_LIBELLE = "libelle";
	
	// PRODUIT Table - column names
	private static final String KEY_SKU = "sku";
	private static final String KEY_CATEGORIE_ID = "categorie_id";
	private static final String KEY_ADDED_LOCALY = "isAddedLocaly";

	// CATEGORIE Table - column names
	private static final String KEY_CONTEXT = "context";
	private static final String KEY_PARENT_ID = "parent_id";
	
	// MARQUE_CATEGORIE Table - column names
	private static final String KEY_MARQUE_ID = "marque_id";
	//private static final String KEY_CATEGORIE_ID = "KEY_CATEGORIE_ID";
	
	// PDV Table - column names
	private static final String KEY_LICENCE = "licence";
	private static final String KEY_VILLE = "ville";
	private static final String KEY_SECTEUR = "secteur";

	// Localisation Table - column names
	private static final String KEY_ANIMATEUR_ID = "animateur_id";
	private static final String KEY_SUPERVISEUR_ID = "superviseur_id";
	private static final String KEY_PDV_ID = "pdv_id";
	private static final String KEY_IMAGEFILENAME = "imageFileName";
	private static final String KEY_LONGITUDE = "longitude";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LICENCEREMPLACEE = "licenceRemplacee";
	private static final String KEY_MOTIF = "motif";
	private static final String KEY_INSERTED_IN_SERVER_WITH_ID = "insertedInServerWithId";

	// Rapport Table - column names
	private static final String KEY_ACHETE = "achete";
	private static final String KEY_TRANCHEAGE_ID = "trancheage_id";
	private static final String KEY_RAISONACHAT_ID = "raisonachat_id";
	private static final String KEY_RAISONREFUS_ID = "raisonrefus_id";
	private static final String KEY_SEXE = "sexe";
	private static final String KEY_CADEAUX_IDS = "cadeaux_ids";
	private static final String KEY_FIDELITE = "fidelite_id";
	private static final String KEY_MARQUEHABITUELLE_ID = "marquehabituelle_id";
	private static final String KEY_MARQUEHABITUELLE_QTE = "marquehabituelle_qte";
	private static final String KEY_MARQUEACHETEE_ID = "marqueachetee_id";
	private static final String KEY_MARQUEACHETEE_QTE = "marqueachetee_qte";
	private static final String KEY_TOMBOLA = "tombola";
	private static final String KEY_COMMENTAIRE = "commentaire";
	private static final String KEY_LOCALISATION_ID = "localisation_id";
	
	private static final String KEY_POI_ID = "poi_id";
	
	private static final String KEY_QUANTITIES_DATA = "quantitiesData";
	private static final String KEY_TYPE = "type";
	
	private static final String KEY_NBR_LIGNES_TRAITEES = "nbrLignesTraitees";;

	private static final String KEY_TEMPS_REMILSSAGE    = "tempsReplmissage";
	
	// Table Create Statements
	// Marque table create statement
	private static final String CREATE_TABLE_MARQUE = "CREATE TABLE "
			+ TABLE_MARQUE + "(" + KEY_ID + " INTEGER," + KEY_LIBELLE
			+ " TEXT )";

	// Produit table create statement
	private static final String CREATE_TABLE_PRODUIT = "CREATE TABLE "
				+ TABLE_PRODUIT + "(" + KEY_ID + " INTEGER," + KEY_SKU
				+ " TEXT," + KEY_LIBELLE+ " TEXT," + KEY_CATEGORIE_ID+ " TEXT, " + KEY_TYPE + " TEXT, " + KEY_ADDED_LOCALY + " INTEGER )";
	// Categorie table create statement
	private static final String CREATE_TABLE_CATEGORIE = "CREATE TABLE "
					+ TABLE_CATEGORIE + "(" + KEY_ID + " INTEGER," + KEY_NOM
					+ " TEXT," + KEY_CONTEXT+ " TEXT," + KEY_PARENT_ID+ " TEXT )";
	
	// Marque_Categorie table create statement
	private static final String CREATE_TABLE_MARQUE_CATEGORIE = "CREATE TABLE "
						+ TABLE_MARQUE_CATEGORIE + "(" + KEY_MARQUE_ID + " TEXT, " + KEY_CATEGORIE_ID + " TEXT )";
		
	// Cadeau table create statement
	private static final String CREATE_TABLE_CADEAU = "CREATE TABLE "
			+ TABLE_CADEAU + "(" + KEY_ID + " INTEGER ," + KEY_LIBELLE
			+ " TEXT )";

	// PDV table create statement
	private static final String CREATE_TABLE_PDV = "CREATE TABLE " + TABLE_PDV
			+ "(" + KEY_ID + " INTEGER," + KEY_NOM + " TEXT," + KEY_LICENCE
			+ " INTEGER," + KEY_VILLE + " TEXT," + KEY_SECTEUR + " TEXT )";

	// SUPERVISEUR table create statement
	private static final String CREATE_TABLE_SUPERVISEUR = "CREATE TABLE "
			+ TABLE_SUPERVISEUR + "(" + KEY_ID + " INTEGER," + KEY_NOM
			+ " TEXT," + KEY_PRENOM + " TEXT," + KEY_LICENCE + " INTEGER" + ")";
	// Marque table create statement
	private static final String CREATE_TABLE_RAISONACHAT = "CREATE TABLE "
			+ TABLE_RAISONACHAT + "(" + KEY_ID + " INTEGER," + KEY_LIBELLE
			+ " TEXT )";
	// Marque table create statement
	private static final String CREATE_TABLE_RAISONREFU = "CREATE TABLE "
			+ TABLE_RAISONREFUS + "(" + KEY_ID + " INTEGER," + KEY_LIBELLE
			+ " TEXT )";
	// Marque table create statement
	private static final String CREATE_TABLE_TRANCHEAGE = "CREATE TABLE "
			+ TABLE_TRANCHEAGE + "(" + KEY_ID + " INTEGER," + KEY_LIBELLE
			+ " TEXT )";
	// Localisation table create statement
	private static final String CREATE_TABLE_LOCALISATION = "CREATE TABLE "
			+ TABLE_LOCALISATION + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_IMAGEFILENAME + " TEXT," + KEY_LONGITUDE + " TEXT,"
			+ KEY_LATITUDE + " TEXT," + KEY_ANIMATEUR_ID + " TEXT,"
			+ KEY_SUPERVISEUR_ID + " TEXT," + KEY_PDV_ID + " TEXT,"
			+ KEY_LICENCEREMPLACEE + " TEXT," + KEY_MOTIF + " TEXT," + KEY_INSERTED_IN_SERVER_WITH_ID + " TEXT," + KEY_DATE_CREATION +" TEXT)";

	// Rapport table create statement
	private static final String CREATE_TABLE_RAPPORT = "CREATE TABLE "
			+ TABLE_RAPPORT + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_ACHETE + " TEXT," + KEY_TRANCHEAGE_ID + " TEXT," + KEY_SEXE
			+ " TEXT," + KEY_FIDELITE + " TEXT, " + KEY_RAISONACHAT_ID
			+ " TEXT," + KEY_RAISONREFUS_ID + " TEXT,"
			+ KEY_MARQUEHABITUELLE_ID + " TEXT," + KEY_MARQUEHABITUELLE_QTE
			+ " TEXT," + KEY_MARQUEACHETEE_ID + " TEXT ,"
			+ KEY_MARQUEACHETEE_QTE + " TEXT ," + KEY_CADEAUX_IDS + " TEXT,"
			+ KEY_COMMENTAIRE + " TEXT ," + KEY_TOMBOLA + " TEXT ," + KEY_LOCALISATION_ID + " TEXT," + KEY_DATE_CREATION +" TEXT)";

	// Pdv_Poi table create statement
	private static final String CREATE_TABLE_PDV_POI = "CREATE TABLE "
							+ TABLE_PDV_POI + "(" + KEY_PDV_ID + " TEXT, " + KEY_POI_ID + " TEXT )";
	
	// Poi table create statement
	private static final String CREATE_TABLE_POI = "CREATE TABLE "
				+ TABLE_POI + "(" + KEY_ID + " INTEGER," + KEY_LIBELLE
				+ " TEXT )";
	
	// Questionnaire table create statement
	private static final String CREATE_TABLE_QUESTIONNAIRE = "CREATE TABLE "
				+ TABLE_QUESTIONNAIRE + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
				+ KEY_TYPE + " TEXT," + KEY_QUANTITIES_DATA + " TEXT," + KEY_LOCALISATION_ID + " TEXT," + KEY_DATE_CREATION +" TEXT, " 
				+ KEY_NBR_LIGNES_TRAITEES + " INTEGER," + KEY_TEMPS_REMILSSAGE + " FLOAT)";
		
	public SqliteDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating required tables
		db.execSQL(CREATE_TABLE_MARQUE);
		db.execSQL(CREATE_TABLE_CATEGORIE);
		db.execSQL(CREATE_TABLE_MARQUE_CATEGORIE);
		db.execSQL(CREATE_TABLE_PRODUIT);
		db.execSQL(CREATE_TABLE_PDV);
		db.execSQL(CREATE_TABLE_CADEAU);
		db.execSQL(CREATE_TABLE_SUPERVISEUR);
		db.execSQL(CREATE_TABLE_RAISONREFU);
		db.execSQL(CREATE_TABLE_RAISONACHAT);
		db.execSQL(CREATE_TABLE_TRANCHEAGE);
		db.execSQL(CREATE_TABLE_LOCALISATION);
		db.execSQL(CREATE_TABLE_RAPPORT);
		db.execSQL(CREATE_TABLE_PDV_POI);
		db.execSQL(CREATE_TABLE_POI);
		db.execSQL(CREATE_TABLE_QUESTIONNAIRE);
		// db.execSQL(CREATE_TABLE_TODO_TAG);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARQUE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARQUE_CATEGORIE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUIT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PDV);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CADEAU);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUPERVISEUR);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RAISONACHAT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RAISONREFUS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANCHEAGE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCALISATION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RAPPORT);
		// create new tables
		onCreate(db);
	}

	// ------------------------ "cadeau" table methods ----------------//

	public long createCadeau(Cadeau cadeau) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, cadeau.getId());
		values.put(KEY_LIBELLE, cadeau.getLibelle());

		// insert row
		long pdv_id = db.insert(TABLE_CADEAU, null, values);

		return pdv_id;
	}

	public Cadeau getCadeau(long cadeau_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_CADEAU + " WHERE "
				+ KEY_ID + " = " + cadeau_id;

		//Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();
		
		Cadeau cadeau = new Cadeau();
		cadeau.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		cadeau.setLibelle((c.getString(c.getColumnIndex(KEY_LIBELLE))));
		
		c.close();
		
		return cadeau;
	}

	public List<Cadeau> getAllCadeaux() {
		List<Cadeau> cadeaux = new ArrayList<Cadeau>();
		String selectQuery = "SELECT  * FROM " + TABLE_CADEAU;

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Cadeau cadeau = new Cadeau();
				cadeau.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				cadeau.setLibelle((c.getString(c.getColumnIndex(KEY_LIBELLE))));

				// adding to todo list
				cadeaux.add(cadeau);
			} while (c.moveToNext());
		}

		c.close();
		return cadeaux;
	}

	// ------------------------ "marque" table methods ----------------//

	public long createMarque(Marque marque) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, marque.getId());
		values.put(KEY_LIBELLE, marque.getLibelle());

		// insert row
		long pdv_id = db.insert(TABLE_MARQUE, null, values);

		return pdv_id;
	}

	public Marque getMarque(long marque_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_MARQUE + " WHERE "
				+ KEY_ID + " = " + marque_id;

		//Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Marque marque = new Marque();
		marque.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		marque.setLibelle((c.getString(c.getColumnIndex(KEY_LIBELLE))));
		c.close();
		return marque;
	}

	public List<Marque> getAllMarques() {
		List<Marque> marques = new ArrayList<Marque>();
		String selectQuery = "SELECT  * FROM " + TABLE_MARQUE;

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Marque marque = new Marque();
				marque.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				marque.setLibelle((c.getString(c.getColumnIndex(KEY_LIBELLE))));

				// adding to todo list
				marques.add(marque);
			} while (c.moveToNext());
		}
		c.close();
		
		return marques;
	}
	
	public List<Marque> getAllMarquesOperatingInCategory(Categorie categorie) {
		List<Marque> marques = new ArrayList<Marque>();
		for(Marque marque : getAllMarques()){
			for(MarqueCategorie marque_categorie : getAllMarquesCategories()){
				if(marque.getId() == Integer.valueOf(marque_categorie.getMarqueId()) && categorie.getId() == Integer.valueOf(marque_categorie.getCategorieId())){
					marques.add(marque);
					break;
				}
			}
		}

		return marques;
	}
	
	// ------------------------ "marque" table methods ----------------//

	public long createPoi(Poi poi) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, poi.getId());
		values.put(KEY_LIBELLE, poi.getLibelle());

		// insert row
		long pdv_id = db.insert(TABLE_POI, null, values);

		return pdv_id;
	}

	public Poi getPoi(long poi_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_POI + " WHERE "
				+ KEY_ID + " = " + poi_id;

		//Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Poi poi = new Poi();
		poi.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		poi.setLibelle((c.getString(c.getColumnIndex(KEY_LIBELLE))));
		c.close();
		return poi;
	}

	public List<Poi> getAllPois() {
		List<Poi> pois = new ArrayList<Poi>();
		String selectQuery = "SELECT  * FROM " + TABLE_POI;
		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Poi poi = new Poi();
				poi.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				poi.setLibelle((c.getString(c.getColumnIndex(KEY_LIBELLE))));

				// adding to todo list
				pois.add(poi);
			} while (c.moveToNext());
		}
		c.close();
		return pois;
	}
	
	
	// ------------------------ "PRODUIT" table methods ----------------//

	public long createProduit(Produit produit) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, produit.getId());
		values.put(KEY_SKU, produit.getSku());
		values.put(KEY_LIBELLE, produit.getLibelle());
		values.put(KEY_CATEGORIE_ID, produit.getCategorieId());
		values.put(KEY_ADDED_LOCALY, (produit.isAddedLocaly())?1:0);
		values.put(KEY_TYPE, produit.getType());
		// insert row
		long id = db.insert(TABLE_PRODUIT, null, values);

		return id;
	}

	public Produit getProduit(long produit_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_PRODUIT + " WHERE "
				+ KEY_ID + " = " + produit_id;

		//Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Produit produit = new Produit();
		produit.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		produit.setLibelle((c.getString(c.getColumnIndex(KEY_LIBELLE))));
		produit.setSku((c.getString(c.getColumnIndex(KEY_SKU))));
		produit.setCategorieId((c.getString(c.getColumnIndex(KEY_CATEGORIE_ID))));
		produit.setAddedLocaly((c.getInt(c.getColumnIndex(KEY_ADDED_LOCALY))));
		produit.setType((c.getString(c.getColumnIndex(KEY_TYPE))));
		c.close();
		return produit;
	}

	public List<Produit> getAllProduits() {
		List<Produit> produits = new ArrayList<Produit>();
		String selectQuery = "SELECT  * FROM " + TABLE_PRODUIT;

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Produit produit = new Produit();
				produit.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				produit.setLibelle((c.getString(c.getColumnIndex(KEY_LIBELLE))));
				produit.setSku((c.getString(c.getColumnIndex(KEY_SKU))));
				produit.setCategorieId((c.getString(c.getColumnIndex(KEY_CATEGORIE_ID))));
				produit.setAddedLocaly((c.getInt(c.getColumnIndex(KEY_ADDED_LOCALY))));
				produit.setType((c.getString(c.getColumnIndex(KEY_TYPE))));
				// adding to todo list
				produits.add(produit);
			} while (c.moveToNext());
		}
		c.close();
		return produits;
	}
	
	public List<Produit> getAllProduits(String[] segmentsIds) {
		List<Produit> produits = new ArrayList<Produit>();
		String selectQuery = "SELECT  * FROM " + TABLE_PRODUIT;

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Produit produit = new Produit();
				produit.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				produit.setLibelle((c.getString(c.getColumnIndex(KEY_LIBELLE))));
				produit.setSku((c.getString(c.getColumnIndex(KEY_SKU))));
				produit.setCategorieId((c.getString(c.getColumnIndex(KEY_CATEGORIE_ID))));
				produit.setAddedLocaly((c.getInt(c.getColumnIndex(KEY_ADDED_LOCALY))));
				produit.setType((c.getString(c.getColumnIndex(KEY_TYPE))));
				
				for(String segmentId : segmentsIds){
					if(produit.getCategorieId().equals(segmentId)){
						// adding to todo list
						produits.add(produit);
						break;
					}
				}
			} while (c.moveToNext());
		}
		c.close();
		return produits;
	}
	
	// ------------------------ "CATEGORIE" table methods ----------------//

	public long createCategorie(Categorie categorie) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, categorie.getId());
		values.put(KEY_NOM, categorie.getNom());
		values.put(KEY_CONTEXT, categorie.getContext());
		values.put(KEY_PARENT_ID, categorie.getParentId());

		// insert row
		long id = db.insert(TABLE_CATEGORIE, null, values);
		
		return id;
	}

	public Categorie getCategorie(long categorie_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIE + " WHERE "
				+ KEY_ID + " = " + categorie_id;

		//Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Categorie categorie = new Categorie();
		categorie.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		categorie.setContext((c.getString(c.getColumnIndex(KEY_CONTEXT))));
		categorie.setNom((c.getString(c.getColumnIndex(KEY_NOM))));
		categorie.setParentId((c.getString(c.getColumnIndex(KEY_PARENT_ID))));
		c.close();
		return categorie;
	}

	public List<Categorie> getAllCategories() {
		List<Categorie> categories = new ArrayList<Categorie>();
		String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIE;

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Categorie categorie = new Categorie();
				categorie.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				categorie.setContext((c.getString(c.getColumnIndex(KEY_CONTEXT))));
				categorie.setNom((c.getString(c.getColumnIndex(KEY_NOM))));
				categorie.setParentId((c.getString(c.getColumnIndex(KEY_PARENT_ID))));

				// adding to todo list
				categories.add(categorie);
			} while (c.moveToNext());
		}
		c.close();
		return categories;
	}
	
	public List<Categorie> getAllCategoriesOfProduits() {
		List<Categorie> categories = new ArrayList<Categorie>();
		for(Categorie categorie : getAllCategories()){
			for(MarqueCategorie marque_categorie : getAllMarquesCategories()){
				if(categorie.getId() == Integer.valueOf(marque_categorie.getCategorieId())){
					categories.add(categorie);
					break;
				}
			}
		}
		return categories;
	}
	
	public List<Categorie> getSegmentsOfCategorie(Categorie categorieProduits) {
		return getSegmentsOfCategorie(categorieProduits, new ArrayList<Categorie>());
	}
	
	public List<Categorie> getSegmentsOfCategorie(Categorie categorieProduits, List<Categorie> resultOfLastExecution) {
		List<Categorie> categoriesList = getAllCategories();
		List<Categorie> result = new ArrayList<Categorie>();
		if(resultOfLastExecution!= null){
			result.addAll(resultOfLastExecution);
		}
		
		for(Categorie categorie : categoriesList){
			//Log.d("getSegmentsOfCategorie", categorie.getNom() + ", "  +categorie.getParentId() + "," + categorieProduits.getId());
			if(categorie.getParentId() != null && !categorie.getParentId().equals("null")
					&& !categorie.getParentId().isEmpty() && categorieProduits != null){
				if(Integer.valueOf(categorie.getParentId()) == categorieProduits.getId()){
					if(getChildren(categorie, categoriesList).size() >0)
						result.addAll(getSegmentsOfCategorie(categorie, result));
					else
						result.add(categorie);
				}
			}
		}
		// si la catégorie de produits n'a pas de segments alors elle est considérée comme segment
		if(result.size() <= 0){
			result.add(categorieProduits);
		}
		return result;
	}
	
	public List<Categorie> getChildren(Categorie parentCategorie, List<Categorie> categories) {
		List<Categorie> children = new ArrayList<Categorie>();
		for(Categorie categorie : categories){
			if(categorie.getParentId() != null && !categorie.getParentId().equals("null")
			   && !categorie.getParentId().isEmpty() && parentCategorie != null){
				if(Integer.valueOf(categorie.getParentId()) == parentCategorie.getId()){
					children.add(categorie);
				}
			}
		}
		return children;
	}
	
	// ------------------------ "PDV_POI" table methods ----------------//

	public long createPdvPoi(PdvPoi pdv_poi) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_PDV_ID, pdv_poi.getPdvId());
		values.put(KEY_POI_ID, pdv_poi.getPoiId());

		// insert row
		long id = db.insert(TABLE_PDV_POI, null, values);

		return id;
	}

	public PdvPoi getPdvPoi(String pdv_id, String poi_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_PDV_POI + " WHERE "
				+ KEY_PDV_ID + " = '" + pdv_id + "' AND"
				+ KEY_POI_ID + " = '" + poi_id + "'";

		//Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();
		PdvPoi pdv_poi = new PdvPoi();
		pdv_poi.setPdvId((c.getString(c.getColumnIndex(KEY_PDV_ID))));
		pdv_poi.setPoiId((c.getString(c.getColumnIndex(KEY_POI_ID))));
		c.close();
		return pdv_poi;
	}

	public List<PdvPoi> getAllPdvsPois() {
		List<PdvPoi> pdvs_pois = new ArrayList<PdvPoi>();
		String selectQuery = "SELECT  * FROM " + TABLE_PDV_POI;
		//Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				PdvPoi pdv_poi = new PdvPoi();
				pdv_poi.setPdvId((c.getString(c.getColumnIndex(KEY_PDV_ID))));
				pdv_poi.setPoiId((c.getString(c.getColumnIndex(KEY_POI_ID))));
				pdvs_pois.add(pdv_poi);
			} while (c.moveToNext());
		}
		c.close();
		return pdvs_pois;
	}
		
	
	// ------------------------ "MARQUE_CATEGORIE" table methods ----------------//

	public long createMarqueCategorie(MarqueCategorie categorie) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_MARQUE_ID, categorie.getMarqueId());
		values.put(KEY_CATEGORIE_ID, categorie.getCategorieId());

		// insert row
		long id = db.insert(TABLE_MARQUE_CATEGORIE, null, values);

		return id;
	}

	public MarqueCategorie getMarqueCategorie(String marque_id, String categorie_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_MARQUE_CATEGORIE + " WHERE "
				+ KEY_MARQUE_ID + " = '" + marque_id + "' AND"
				+ KEY_CATEGORIE_ID + " = '" + categorie_id + "'";

		//Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();
		MarqueCategorie marque_categorie = new MarqueCategorie();
		marque_categorie.setMarqueId((c.getString(c.getColumnIndex(KEY_MARQUE_ID))));
		marque_categorie.setCategorieId((c.getString(c.getColumnIndex(KEY_CATEGORIE_ID))));
		c.close();
		return marque_categorie;
	}

	public List<MarqueCategorie> getAllMarquesCategories() {
		List<MarqueCategorie> marques_categories = new ArrayList<MarqueCategorie>();
		String selectQuery = "SELECT  * FROM " + TABLE_MARQUE_CATEGORIE;
		//Log.e(LOG, selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				MarqueCategorie marque_categorie = new MarqueCategorie();
				marque_categorie.setMarqueId((c.getString(c.getColumnIndex(KEY_MARQUE_ID))));
				marque_categorie.setCategorieId((c.getString(c.getColumnIndex(KEY_CATEGORIE_ID))));
				marques_categories.add(marque_categorie);
			} while (c.moveToNext());
		}
		c.close();
		return marques_categories;
	}

	// ------------------------ "pdv" table methods ----------------//

	public long createPDV(Pdv pdv) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, pdv.getId());
		values.put(KEY_NOM, pdv.getNom());
		values.put(KEY_LICENCE, pdv.getLicence());
		values.put(KEY_VILLE, pdv.getVille());
		values.put(KEY_SECTEUR, pdv.getSecteur());

		// insert row
		long pdv_id = db.insert(TABLE_PDV, null, values);

		return pdv_id;
	}

	public Pdv getPDV(long pdv_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_PDV + " WHERE " + KEY_ID
				+ " = " + pdv_id;

		//Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Pdv pdv = new Pdv();
		pdv.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		pdv.setNom((c.getString(c.getColumnIndex(KEY_NOM))));
		pdv.setLicence((c.getString(c.getColumnIndex(KEY_LICENCE))));
		pdv.setVille((c.getString(c.getColumnIndex(KEY_VILLE))));
		pdv.setSecteur((c.getString(c.getColumnIndex(KEY_SECTEUR))));
		c.close();
		return pdv;
	}

	public List<Pdv> getAllPDV() {
		List<Pdv> pdvs = new ArrayList<Pdv>();
		String selectQuery = "SELECT  * FROM " + TABLE_PDV;

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Pdv pdv = new Pdv();
				pdv.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				pdv.setNom((c.getString(c.getColumnIndex(KEY_NOM))));
				pdv.setLicence((c.getString(c.getColumnIndex(KEY_LICENCE))));
				pdv.setVille((c.getString(c.getColumnIndex(KEY_VILLE))));
				pdv.setSecteur((c.getString(c.getColumnIndex(KEY_SECTEUR))));
				// adding to todo list
				pdvs.add(pdv);
			} while (c.moveToNext());
		}
		c.close();
		return pdvs;
	}

	public List<Pdv> getPDVsBySecteur(String ville, String secteur) {
		List<Pdv> pdvsOfSecteur = new ArrayList<Pdv>();
		List<Pdv> pdvs = getAllPDV();
		for(Pdv pdv : pdvs){
			if(pdv.getVille().equals(ville) && pdv.getSecteur().equals(secteur)){
				pdvsOfSecteur.add(pdv);
			}
		}
		return pdvsOfSecteur;
	}

	public List<String> getAllVilles() {
		List<String> villes = new ArrayList<String>();
		List<Pdv> pdvs = getAllPDV();
		for(Pdv pdv : pdvs){
			boolean alreadyAdded = false;
			for (String ville : villes){
				if(pdv.getVille().equals(ville) ){
					alreadyAdded = true;
				}
			}
			if(!alreadyAdded) villes.add(pdv.getVille());
		}
		return villes;
	}

	public List<String> getAllSecteurs( String ville) {
		List<String> secteurs = new ArrayList<String>();
		List<Pdv> pdvs = getAllPDV();
		for(Pdv pdv : pdvs){
			if(pdv.getVille().equals(ville)){
				boolean alreadyAdded = false;
				for (String secteur : secteurs){
					if(pdv.getSecteur().equals(secteur) ){
						alreadyAdded = true;
					}
				}
				if(!alreadyAdded) secteurs.add(pdv.getSecteur());
			}
		}
		return secteurs;
	}

	// ------------------------ "superviseur" table methods ----------------//

	public long createSuperviseur(Superviseur Superviseur) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, Superviseur.getId());
		values.put(KEY_NOM, Superviseur.getNom());
		values.put(KEY_PRENOM, Superviseur.getPrenom());

		// insert row
		long Superviseur_id = db.insert(TABLE_SUPERVISEUR, null, values);

		return Superviseur_id;
	}

	public Superviseur getSuperviseur(long Superviseur_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_SUPERVISEUR + " WHERE "
				+ KEY_ID + " = " + Superviseur_id;

		//Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Superviseur Superviseur = new Superviseur();
		Superviseur.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		Superviseur.setNom((c.getString(c.getColumnIndex(KEY_NOM))));
		Superviseur.setNom((c.getString(c.getColumnIndex(KEY_PRENOM))));
		c.close();
		return Superviseur;
	}

	public List<Superviseur> getAllSuperviseurs() {
		List<Superviseur> Superviseurs = new ArrayList<Superviseur>();
		String selectQuery = "SELECT  * FROM " + TABLE_SUPERVISEUR;

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Superviseur Superviseur = new Superviseur();
				Superviseur.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				Superviseur.setNom((c.getString(c.getColumnIndex(KEY_NOM))));
				Superviseur
						.setPrenom((c.getString(c.getColumnIndex(KEY_PRENOM))));

				// add to list
				Superviseurs.add(Superviseur);
			} while (c.moveToNext());
		}
		c.close();
		return Superviseurs;
	}

	// ------------------------ "raisonAchat" table methods ----------------//

	public long createRaisonAchat(RaisonAchat raisonAchat) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, raisonAchat.getId());
		values.put(KEY_LIBELLE, raisonAchat.getLibelle());

		// insert row
		long id = db.insert(TABLE_RAISONACHAT, null, values);

		return id;
	}

	public List<RaisonAchat> getAllRaisonsAchat() {
		List<RaisonAchat> raisonsAchat = new ArrayList<RaisonAchat>();
		String selectQuery = "SELECT  * FROM " + TABLE_RAISONACHAT;

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				RaisonAchat raisonAchat = new RaisonAchat();
				raisonAchat.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				raisonAchat.setLibelle((c.getString(c
						.getColumnIndex(KEY_LIBELLE))));

				// adding to todo list
				raisonsAchat.add(raisonAchat);
			} while (c.moveToNext());
		}
		c.close();
		return raisonsAchat;
	}

	// ------------------------ "raisonRefu" table methods ----------------//

	public long createRaisonRefus(RaisonRefus raisonRefu) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, raisonRefu.getId());
		values.put(KEY_LIBELLE, raisonRefu.getLibelle());

		// insert row
		long id = db.insert(TABLE_RAISONREFUS, null, values);

		return id;
	}

	public List<RaisonRefus> getAllRaisonsRefus() {
		List<RaisonRefus> raisonsRefu = new ArrayList<RaisonRefus>();
		String selectQuery = "SELECT  * FROM " + TABLE_RAISONREFUS;

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				RaisonRefus raisonRefu = new RaisonRefus();
				raisonRefu.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				raisonRefu.setLibelle((c.getString(c
						.getColumnIndex(KEY_LIBELLE))));

				// adding to todo list
				raisonsRefu.add(raisonRefu);
			} while (c.moveToNext());
		}
		c.close();
		return raisonsRefu;
	}

	// ------------------------ "trancheAge" table methods ----------------//

	public long createTrancheAge(TrancheAge trancheAge) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, trancheAge.getId());
		values.put(KEY_LIBELLE, trancheAge.getLibelle());

		// insert row
		long id = db.insert(TABLE_TRANCHEAGE, null, values);

		return id;
	}

	public List<TrancheAge> getAllTranchesAge() {
		List<TrancheAge> tranchesAge = new ArrayList<TrancheAge>();
		String selectQuery = "SELECT  * FROM " + TABLE_TRANCHEAGE;

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				TrancheAge trancheAge = new TrancheAge();
				trancheAge.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				trancheAge.setLibelle((c.getString(c
						.getColumnIndex(KEY_LIBELLE))));

				// adding to todo list
				tranchesAge.add(trancheAge);
			} while (c.moveToNext());
		}
		c.close();
		return tranchesAge;
	}

	// ------------------------ "localisation" table methods ----------------//

	public long createLocalisation(Localisation localisation) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ANIMATEUR_ID, localisation.getSfoId());
		values.put(KEY_SUPERVISEUR_ID, localisation.getSuperviseurId());
		values.put(KEY_PDV_ID, localisation.getPdvId());
		values.put(KEY_IMAGEFILENAME, localisation.getCheminImage());
		values.put(KEY_LONGITUDE, localisation.getLongitude());
		values.put(KEY_LATITUDE, localisation.getLatitude());
		values.put(KEY_LICENCEREMPLACEE, localisation.getLicenceRemplacee());
		values.put(KEY_MOTIF, localisation.getMotif());
		values.put(KEY_INSERTED_IN_SERVER_WITH_ID,"");
		values.put(KEY_DATE_CREATION, localisation.getDateCreation());

		// insert row
		long id = db.insert(TABLE_LOCALISATION, null, values);

		return id;
	}
	
	public int updateLocalisation(Localisation localisation) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_INSERTED_IN_SERVER_WITH_ID, localisation.getInsertedInServerWithId());		

		// updating row
		return db.update(TABLE_LOCALISATION, values, KEY_ID + " = ?",
				new String[] { String.valueOf(localisation.getId()) });
	}


	public List<Localisation> getAllLocalisations() {

		List<Localisation> localisations = new ArrayList<Localisation>();
		String selectQuery = "SELECT  * FROM " + TABLE_LOCALISATION;

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Localisation localisation = new Localisation();
				localisation.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				localisation.setSfoId(c.getString(c
						.getColumnIndex(KEY_ANIMATEUR_ID)));
				localisation.setSuperviseurId(c.getString(c
						.getColumnIndex(KEY_SUPERVISEUR_ID)));
				localisation
						.setPdvId(c.getString(c.getColumnIndex(KEY_PDV_ID)));
				localisation.setCheminImage(c.getString(c
						.getColumnIndex(KEY_IMAGEFILENAME)));
				localisation.setLongitude(c.getString(c
						.getColumnIndex(KEY_LONGITUDE)));
				localisation.setLatitude(c.getString(c
						.getColumnIndex(KEY_LATITUDE)));
				localisation.setLicenceRemplacee(c.getString(c
						.getColumnIndex(KEY_LICENCEREMPLACEE)));
				localisation.setMotif(c.getString(c.getColumnIndex(KEY_MOTIF)));
				
				localisation.setInsertedInServerWithId(c.getString(c.getColumnIndex(KEY_INSERTED_IN_SERVER_WITH_ID)));
				localisation.setDateCreation(c.getString(c.getColumnIndex(KEY_DATE_CREATION)));
				
				// adding to todo list
				localisations.add(localisation);
			} while (c.moveToNext());
		}
		c.close();
		return localisations;
	}

	public void deleteLocalisation(Localisation localisation) {
		this.getWritableDatabase().delete(TABLE_LOCALISATION,
				new String(KEY_ID + "=?"),
				new String[] { String.valueOf(localisation.getId()) });
	}
	
	// ------------------------ "rapport" table methods ----------------//

	public long createRapport(Rapport rapport) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ACHETE, rapport.getAchete());
		values.put(KEY_TRANCHEAGE_ID, rapport.getTrancheAgeId());
		values.put(KEY_SEXE, rapport.getSexe());
		values.put(KEY_FIDELITE, rapport.getFidelite());
		values.put(KEY_RAISONACHAT_ID, rapport.getRaisonAchatId());
		values.put(KEY_RAISONREFUS_ID, rapport.getRaisonRefusId());
		values.put(KEY_MARQUEHABITUELLE_ID, rapport.getMarqueHabituelleId());
		values.put(KEY_MARQUEHABITUELLE_QTE, rapport.getMarqueHabituelleQte());
		values.put(KEY_MARQUEACHETEE_ID, rapport.getMarqueAcheteeId());
		values.put(KEY_MARQUEACHETEE_QTE, rapport.getMarqueAcheteeQte());
		values.put(KEY_CADEAUX_IDS, rapport.getCadeauId());
		values.put(KEY_TOMBOLA, rapport.getTombola());
		values.put(KEY_COMMENTAIRE, rapport.getCommentaire());
		values.put(KEY_LOCALISATION_ID, rapport.getLocalisationId());
		values.put(KEY_DATE_CREATION, rapport.getDateCreation());
		// insert row
		long id = db.insert(TABLE_RAPPORT, null, values);

		return id;
	}

	public List<Rapport> getAllRapports() {

		List<Rapport> rapports = new ArrayList<Rapport>();
		String selectQuery = "SELECT  * FROM " + TABLE_RAPPORT;

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Rapport rapport = new Rapport();
				rapport.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				rapport.setAchete(c.getString(c.getColumnIndex(KEY_ACHETE)));
				rapport.setTrancheAgeId(c.getString(c
						.getColumnIndex(KEY_TRANCHEAGE_ID)));
				rapport.setSexe(c.getString(c.getColumnIndex(KEY_SEXE)));
				rapport.setFidelite(c.getString(c.getColumnIndex(KEY_FIDELITE)));
				rapport.setRaisonAchatId(c.getString(c
						.getColumnIndex(KEY_RAISONACHAT_ID)));
				rapport.setRaisonRefusId(c.getString(c
						.getColumnIndex(KEY_RAISONREFUS_ID)));
				rapport.setMarqueHabituelleId(c.getString(c
						.getColumnIndex(KEY_MARQUEHABITUELLE_ID)));
				rapport.setMarqueHabituelleQte(c.getString(c
						.getColumnIndex(KEY_MARQUEHABITUELLE_QTE)));
				rapport.setMarqueAcheteeId(c.getString(c
						.getColumnIndex(KEY_MARQUEACHETEE_ID)));
				rapport.setMarqueAcheteeQte(c.getString(c
						.getColumnIndex(KEY_MARQUEACHETEE_QTE)));
				rapport.setCadeauId(c.getString(c.getColumnIndex(KEY_CADEAUX_IDS)));
				rapport.setTombola(c.getString(c.getColumnIndex(KEY_TOMBOLA)));
				rapport.setCommentaire(c.getString(c
						.getColumnIndex(KEY_COMMENTAIRE)));				
				rapport.setLocalisationId(c.getString(c
						.getColumnIndex(KEY_LOCALISATION_ID)));
				rapport.setDateCreation(c.getString(c
						.getColumnIndex(KEY_DATE_CREATION)));
				
				// adding to rapports list
				rapports.add(rapport);
			} while (c.moveToNext());
		}
		c.close();
		return rapports;
	}
	
	public List<Rapport> getAllRapportsOfLocalisation(Localisation localisation) {

		List<Rapport> rapports = new ArrayList<Rapport>();
		String selectQuery = "SELECT  * FROM " + TABLE_RAPPORT + " WHERE " + KEY_LOCALISATION_ID + " = ? ";

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, new String[]{String.valueOf(localisation.getId())});
		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Rapport rapport = new Rapport();
				rapport.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				rapport.setAchete(c.getString(c.getColumnIndex(KEY_ACHETE)));
				rapport.setTrancheAgeId(c.getString(c
						.getColumnIndex(KEY_TRANCHEAGE_ID)));
				rapport.setSexe(c.getString(c.getColumnIndex(KEY_SEXE)));
				rapport.setFidelite(c.getString(c.getColumnIndex(KEY_FIDELITE)));
				rapport.setRaisonAchatId(c.getString(c
						.getColumnIndex(KEY_RAISONACHAT_ID)));
				rapport.setRaisonRefusId(c.getString(c
						.getColumnIndex(KEY_RAISONREFUS_ID)));
				rapport.setMarqueHabituelleId(c.getString(c
						.getColumnIndex(KEY_MARQUEHABITUELLE_ID)));
				rapport.setMarqueHabituelleQte(c.getString(c
						.getColumnIndex(KEY_MARQUEHABITUELLE_QTE)));
				rapport.setMarqueAcheteeId(c.getString(c
						.getColumnIndex(KEY_MARQUEACHETEE_ID)));
				rapport.setMarqueAcheteeQte(c.getString(c
						.getColumnIndex(KEY_MARQUEACHETEE_QTE)));
				rapport.setCadeauId(c.getString(c.getColumnIndex(KEY_CADEAUX_IDS)));
				rapport.setTombola(c.getString(c.getColumnIndex(KEY_TOMBOLA)));
				rapport.setCommentaire(c.getString(c
						.getColumnIndex(KEY_COMMENTAIRE)));				
				rapport.setLocalisationId(c.getString(c
						.getColumnIndex(KEY_LOCALISATION_ID)));
				rapport.setDateCreation(c.getString(c
						.getColumnIndex(KEY_DATE_CREATION)));
				
				// adding to rapports list
				rapports.add(rapport);
			} while (c.moveToNext());
		}
		c.close();
		
		return rapports;
	}
	
	public void deleteRapport(Rapport rapport) {
		this.getWritableDatabase().delete(TABLE_RAPPORT,
				new String(KEY_ID + "=?"),
				new String[] { String.valueOf(rapport.getId()) });
	}

	// ------------------------ "questionnaire" table methods ----------------//

	public long createQuestionnaire(Questionnaire questionnaire) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TYPE, questionnaire.getType());
		values.put(KEY_QUANTITIES_DATA, questionnaire.getQuantitiesData());
		values.put(KEY_LOCALISATION_ID, questionnaire.getLocalisationId());
		values.put(KEY_DATE_CREATION, questionnaire.getDateCreation());
		values.put(KEY_NBR_LIGNES_TRAITEES, questionnaire.getNbrLignesTraitees());
		values.put(KEY_TEMPS_REMILSSAGE, questionnaire.getTempsRemplissage());
		// insert row
		long id = db.insert(TABLE_QUESTIONNAIRE, null, values);

		return id;
	}

	public List<Questionnaire> getAllQuestionnaires() {

		List<Questionnaire> questionnaires = new ArrayList<Questionnaire>();
		String selectQuery = "SELECT  * FROM " + TABLE_QUESTIONNAIRE;

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Questionnaire questionnaire = new Questionnaire();
				questionnaire.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				questionnaire.setType(c.getString(c.getColumnIndex(KEY_TYPE)));
				questionnaire.setQuantitiesData(c.getString(c.getColumnIndex(KEY_QUANTITIES_DATA)));
				questionnaire.setLocalisationId(c.getString(c.getColumnIndex(KEY_LOCALISATION_ID)));
				questionnaire.setDateCreation(c.getString(c.getColumnIndex(KEY_DATE_CREATION)));
				questionnaire.setNbrLignesTraitees(c.getInt(c.getColumnIndex(KEY_NBR_LIGNES_TRAITEES)));
				questionnaire.setTempsRemplissage(c.getFloat(c.getColumnIndex(KEY_TEMPS_REMILSSAGE)));
				
				// adding to questionnaires list
				questionnaires.add(questionnaire);
			} while (c.moveToNext());
		}
		c.close();
		return questionnaires;
	}
	
	public List<Questionnaire> getAllQuestionnairesOfLocalisation(Localisation localisation) {

		List<Questionnaire> questionnaires = new ArrayList<Questionnaire>();
		String selectQuery = "SELECT  * FROM " + TABLE_QUESTIONNAIRE + " WHERE " + KEY_LOCALISATION_ID + " = ? ";

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, new String[]{String.valueOf(localisation.getId())});
		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Questionnaire questionnaire = new Questionnaire();
				questionnaire.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				questionnaire.setType(c.getString(c.getColumnIndex(KEY_TYPE)));
				questionnaire.setQuantitiesData(c.getString(c.getColumnIndex(KEY_QUANTITIES_DATA)));		
				questionnaire.setLocalisationId(c.getString(c
						.getColumnIndex(KEY_LOCALISATION_ID)));
				questionnaire.setDateCreation(c.getString(c
						.getColumnIndex(KEY_DATE_CREATION)));
				questionnaire.setNbrLignesTraitees(c.getInt(c.getColumnIndex(KEY_NBR_LIGNES_TRAITEES)));
				questionnaire.setTempsRemplissage(c.getFloat(c.getColumnIndex(KEY_TEMPS_REMILSSAGE)));
				// adding to questionnaires list
				questionnaires.add(questionnaire);
			} while (c.moveToNext());
		}
		c.close();
		return questionnaires;
	}
	
	public void deleteQuestionnaire(Questionnaire questionnaire) {
		this.getWritableDatabase().delete(TABLE_QUESTIONNAIRE,
				new String(KEY_ID + "=?"),
				new String[] { String.valueOf(questionnaire.getId()) });
	}
	
	
	/* ************************************************************************************************************
	 * getting todo count
	 * *******************************************************
	 * ****************************************************
	 */
	public int getRecordsCount(String tableName) {
		String countQuery = "SELECT  * FROM " + tableName;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();
		// return count
		return count;
	}

	/* ************************************************************************************************************** */

	// closing database
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

	/**
	 * get datetime
	 * */
	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	public void purgeServerFeedData() {
		this.getWritableDatabase().delete(TABLE_CADEAU, null, null);
		this.getWritableDatabase().delete(TABLE_MARQUE, null, null);
		this.getWritableDatabase().delete(TABLE_PRODUIT, null, null);
		this.getWritableDatabase().delete(TABLE_CATEGORIE, null, null);
		this.getWritableDatabase().delete(TABLE_MARQUE_CATEGORIE, null, null);
		this.getWritableDatabase().delete(TABLE_PDV, null, null);
		this.getWritableDatabase().delete(TABLE_SUPERVISEUR, null, null);
		this.getWritableDatabase().delete(TABLE_RAISONACHAT, null, null);
		this.getWritableDatabase().delete(TABLE_RAISONREFUS, null, null);
		this.getWritableDatabase().delete(TABLE_TRANCHEAGE, null, null);
		this.getWritableDatabase().delete(TABLE_POI, null, null);
		this.getWritableDatabase().delete(TABLE_PDV_POI, null, null);
		this.getWritableDatabase().delete(TABLE_QUESTIONNAIRE, null, null);
	}	
}
