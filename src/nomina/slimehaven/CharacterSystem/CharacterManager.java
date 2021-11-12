package nomina.slimehaven.CharacterSystem;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import nomina.evermoreknights.SharedClass.BasicSmartFoxResponse;
import nomina.evermoreknights.SharedClass.GeneralUtility;
import nomina.evermoreknights.SharedClass.MongoDBManager;
import nomina.evermoreknights.SharedClass.References;

public class CharacterManager {
	
	private static CharacterManager instance;
	
	public static CharacterManager Instance() {
		if (instance == null) {
			synchronized (CharacterManager.class) { // thread-safe
				if (instance == null) instance = new CharacterManager();				
			}
		}
		
		return instance;
	}
	
	public List<CharacterAsset> characters = new ArrayList<CharacterAsset>();
	public List<SlotAsset> bodyParts = new ArrayList<SlotAsset>();
	
	public void Initialize() {
		InitializeCharacters();
		InitializeBodyParts();
	}

	private void InitializeCharacters() {
		MongoCollection<Document> characterCollection = MongoDBManager.getInstance().getDBManager().getCollection(References.DatabaseCollection.GameAsset_Character);
		
		FindIterable<Document> docs = characterCollection.find();
		
		characters.clear();
		
		for (Document document : docs) {
			
			CharacterAsset asset = GeneralUtility.ConvertFromDocument(document, CharacterAsset.class);
			
			characters.add(asset);
			
//			System.out.println(String.format("Add Character.randomSize: %s, Element: %s, Type: %s",asset.randomizedBodyParts.size(), ElementType.getEnum(asset.element).toString(), CharacterType.getEnum(asset.charType).toString()));
//			System.out.println(String.format("Add Character.randomSize: %s",asset.randomizedBodyParts.size()));
		}
		
		System.out.println(String.format("CharacterAsset Count: %d", characters.size()));
	}
	
	private void InitializeBodyParts() {
		MongoCollection<Document> bodyPartsCollection = MongoDBManager.getInstance().getDBManager().getCollection(References.DatabaseCollection.GameAsset_BodyPart);
		
		FindIterable<Document> docs = bodyPartsCollection.find();
		
		bodyParts.clear();
		
		for (Document document : docs) {
			
			SlotAsset asset = GeneralUtility.ConvertFromDocument(document, SlotAsset.class);
			
//			System.out.println(GeneralUtility.getGson().toJson(asset));
			
			bodyParts.add(asset);
		}
		
		System.out.println(String.format("SlotAsset Count: %d", bodyParts.size()));
	}
	
	public SlotAsset GetRandomBodyPart(CharacterType charType, ElementType elementType, BodyPartType bodyPart) {
		Predicate<SlotAsset> filter_charType = asset -> asset.characterType == charType.getValue();
		Predicate<SlotAsset> filter_element = asset -> asset.elements.contains(elementType.getValue());
		Predicate<SlotAsset> filter_bodyPart = asset -> asset.slotPairType == bodyPart.getValue();		
		Predicate<SlotAsset> finalFilter = filter_charType.and(filter_element).and(filter_bodyPart);
		
		SlotAsset target = GeneralUtility.RandomElement(GeneralUtility.FilterWithPredicate(bodyParts, finalFilter));
        return target;
    }
	
	public CharacterAsset GetCharacterAsset(CharacterType charType, ElementType elementType) {
		Predicate<CharacterAsset> filter_charType = asset -> asset.charType == charType.getValue() && asset.element == elementType.getValue();
//		Predicate<CharacterAsset> filter_element = asset -> asset.element == elementType;	
//		Predicate<CharacterAsset> finalFilter = filter_charType.and(filter_element);
		System.out.println("raw chracters count: " +characters.size());
		System.out.println("filtering chracters type: " +charType.toString() + " and element: " + elementType.toString());
		
		List<CharacterAsset> total = GeneralUtility.FilterWithPredicate(characters, filter_charType);
		
		System.out.println("filtered chracters count: " +total.size());
		
		CharacterAsset target = total.get(0);
        return target;
    }
	
	
	public BasicSmartFoxResponse GenerateCharacterData(CharacterType charType, ElementType elementType) {
		
		BasicSmartFoxResponse response = new BasicSmartFoxResponse();
		
		CharacterAsset asset = GetCharacterAsset(charType, elementType);
		
		if(asset == null) {
			response.status = 0;
			response.message = String.format("Could not find asset for CharacterType: %s and Element: %s", charType.toString(), elementType.toString());
			return response;
		}
		
		try {
			response.status = 1;
			response.data = asset.GenerateCharacterData();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			
			response.status = 0;
			response.message = e.getStackTrace().toString();
		} 
		
		return response;
	}
}
