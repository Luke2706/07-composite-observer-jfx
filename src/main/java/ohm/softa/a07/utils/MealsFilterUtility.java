package ohm.softa.a07.utils;

import ohm.softa.a07.model.Meal;

import java.util.LinkedList;
import java.util.List;

public abstract class MealsFilterUtility {

	public static List<Meal> filterForVegetarin(List<Meal> meallist){
		List<Meal> vegList = new LinkedList<>();

		for(Meal m : meallist){
			if(m.isVegetarian()){

				vegList.add(m);
			}
		}
		return vegList;
	}
}
