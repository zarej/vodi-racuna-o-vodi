package rs.hakaton.stedivodu;

public class Question {
	String question;
	String answer1;
	String answer2;
	String answer3;
	String answer4;
	int correctAnswer;
	String opis;
	
	public Question(String question, String answer1, String answer2,
			String answer3, String answer4, int correctAnswer, String opis) {
		super();
		this.question = question;
		this.answer1 = answer1;
		this.answer2 = answer2;
		this.answer3 = answer3;
		this.answer4 = answer4;
		this.correctAnswer = correctAnswer;
		this.opis = opis;
	}
}
