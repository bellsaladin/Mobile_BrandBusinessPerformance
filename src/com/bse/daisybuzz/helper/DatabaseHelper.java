package com.bse.daisybuzz.helper;

import com.bse.daizybuzz.model.Cadeau;
import com.bse.daizybuzz.model.Marque;
import com.bse.daizybuzz.model.PDV;
import com.bse.daizybuzz.model.Superviseur;
import com.bse.daizybuzz.model.Tag;
import com.bse.daizybuzz.model.Todo;

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

public class DatabaseHelper extends SQLiteOpenHelper {

	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "contactsManager";

	// Table Names
	private static final String TABLE_MARQUE = "marque";
	private static final String TABLE_PDV = "pdv";
	private static final String TABLE_LOCALISATION = "localisation";
	private static final String TABLE_CADEAU = "cadeau";
	private static final String TABLE_SUPERVISEUR = "superviseur";	

	// Common column names
	private static final String KEY_ID = "id";
	private static final String KEY_NOM = "nom";
	private static final String KEY_PRENOM = "prenom";
	private static final String KEY_CREATED_AT = "created_at";

	// MARQUE Table - column nmaes
	private static final String KEY_LIBELLE = "libelle";

	// PDV Table - column names
	private static final String KEY_LICENCE = "licence";

	// NOTE_TAGS Table - column names
	private static final String KEY_TODO_ID = "todo_id";
	private static final String KEY_TAG_ID = "tag_id";

	// Table Create Statements
	// Marque table create statement
	private static final String CREATE_TABLE_MARQUE = "CREATE TABLE "
			+ TABLE_MARQUE + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_LIBELLE + " TEXT )";

	// Marque table create statement
	private static final String CREATE_TABLE_CADEAU = "CREATE TABLE "
			+ TABLE_CADEAU + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_LIBELLE + " TEXT )";

	// PDV table create statement
	private static final String CREATE_TABLE_PDV = "CREATE TABLE " + TABLE_PDV
			+ "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOM + " TEXT,"
			+ KEY_LICENCE + " INTEGER" + ")";
	
	// SUPERVISEUR table create statement
		private static final String CREATE_TABLE_SUPERVISEUR = "CREATE TABLE " + TABLE_SUPERVISEUR
				+ "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOM + " TEXT," + KEY_PRENOM + " TEXT,"
				+ KEY_LICENCE + " INTEGER" + ")";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// creating required tables
		db.execSQL(CREATE_TABLE_MARQUE);
		db.execSQL(CREATE_TABLE_PDV);
		db.execSQL(CREATE_TABLE_CADEAU);
		db.execSQL(CREATE_TABLE_SUPERVISEUR);
		// db.execSQL(CREATE_TABLE_TODO_TAG);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARQUE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PDV);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CADEAU);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUPERVISEUR);
		// db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO_TAG);

		// create new tables
		onCreate(db);
	}

	// ------------------------ "cadeau" table methods ----------------//

	public long createCadeau(Cadeau cadeau) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_LIBELLE, cadeau.getLibelle());

		// insert row
		long pdv_id = db.insert(TABLE_CADEAU, null, values);

		return pdv_id;
	}

	public Cadeau getCadeau(long cadeau_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_CADEAU + " WHERE "
				+ KEY_ID + " = " + cadeau_id;

		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Cadeau cadeau = new Cadeau();
		cadeau.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		cadeau.setLibelle((c.getString(c.getColumnIndex(KEY_LIBELLE))));

		return cadeau;
	}

	public List<Cadeau> getAllCadeaux() {
		List<Cadeau> cadeaux = new ArrayList<Cadeau>();
		String selectQuery = "SELECT  * FROM " + TABLE_CADEAU;

		Log.e(LOG, selectQuery);

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

		return cadeaux;
	}

	// ------------------------ "marque" table methods ----------------//

	public long createMarque(Marque marque) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_LIBELLE, marque.getLibelle());

		// insert row
		long pdv_id = db.insert(TABLE_MARQUE, null, values);

		return pdv_id;
	}

	public Marque getMarque(long marque_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_MARQUE + " WHERE "
				+ KEY_ID + " = " + marque_id;

		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Marque marque = new Marque();
		marque.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		marque.setLibelle((c.getString(c.getColumnIndex(KEY_LIBELLE))));

		return marque;
	}

	public List<Marque> getAllMarques() {
		List<Marque> marques = new ArrayList<Marque>();
		String selectQuery = "SELECT  * FROM " + TABLE_MARQUE;

		Log.e(LOG, selectQuery);

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

		return marques;
	}

	// ------------------------ "pdv" table methods ----------------//

	public long createPDV(PDV pdv) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NOM, pdv.getNom());
		values.put(KEY_LICENCE, pdv.getLicence());

		// insert row
		long pdv_id = db.insert(TABLE_PDV, null, values);

		return pdv_id;
	}

	public PDV getPDV(long pdv_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_PDV + " WHERE " + KEY_ID
				+ " = " + pdv_id;

		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		PDV pdv = new PDV();
		pdv.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		pdv.setNom((c.getString(c.getColumnIndex(KEY_NOM))));
		pdv.setLicence((c.getInt(c.getColumnIndex(KEY_LICENCE))));

		return pdv;
	}

	public List<PDV> getAllPDV() {
		List<PDV> pdvs = new ArrayList<PDV>();
		String selectQuery = "SELECT  * FROM " + TABLE_PDV;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				PDV pdv = new PDV();
				pdv.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				pdv.setNom((c.getString(c.getColumnIndex(KEY_NOM))));
				pdv.setLicence((c.getInt(c.getColumnIndex(KEY_LICENCE))));

				// adding to todo list
				pdvs.add(pdv);
			} while (c.moveToNext());
		}

		return pdvs;
	}
	
	// ------------------------ "superviseur" table methods ----------------//

		public long createSuperviseur(Superviseur Superviseur) {
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(KEY_NOM, Superviseur.getNom());
			values.put(KEY_PRENOM, Superviseur.getPrenom());

			// insert row
			long Superviseur_id = db.insert(TABLE_SUPERVISEUR, null, values);

			return Superviseur_id;
		}

		public Superviseur getSuperviseur(long Superviseur_id) {
			SQLiteDatabase db = this.getReadableDatabase();

			String selectQuery = "SELECT  * FROM " + TABLE_SUPERVISEUR + " WHERE " + KEY_ID
					+ " = " + Superviseur_id;

			Log.e(LOG, selectQuery);

			Cursor c = db.rawQuery(selectQuery, null);

			if (c != null)
				c.moveToFirst();

			Superviseur Superviseur = new Superviseur();
			Superviseur.setId(c.getInt(c.getColumnIndex(KEY_ID)));
			Superviseur.setNom((c.getString(c.getColumnIndex(KEY_NOM))));	
			Superviseur.setNom((c.getString(c.getColumnIndex(KEY_PRENOM))));	

			return Superviseur;
		}

		public List<Superviseur> getAllSuperviseurs() {
			List<Superviseur> Superviseurs = new ArrayList<Superviseur>();
			String selectQuery = "SELECT  * FROM " + TABLE_SUPERVISEUR;

			Log.e(LOG, selectQuery);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor c = db.rawQuery(selectQuery, null);

			// looping through all rows and adding to list
			if (c.moveToFirst()) {
				do {
					Superviseur Superviseur = new Superviseur();
					Superviseur.setId(c.getInt(c.getColumnIndex(KEY_ID)));
					Superviseur.setNom((c.getString(c.getColumnIndex(KEY_NOM))));					
					Superviseur.setPrenom((c.getString(c.getColumnIndex(KEY_PRENOM))));
					
					// add to list
					Superviseurs.add(Superviseur);
				} while (c.moveToNext());
			}

			return Superviseurs;
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
}
