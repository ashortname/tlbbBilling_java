package tModel;

public class userAccount {
    private int Id;
    private String Name;
    private String PassWord;
    private String Question;
    private String Answer;
    private String Email;
    private String Qq;
    private int Point;
    private byte IsOnline;
    private byte IsLock;

    public enum QueryCode{
        UserNotFound,
        UserFound,
        DbError
    }

    public userAccount()
    {
        Name = "";
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassWord() {
        return PassWord;
    }

    public void setPassWord(String passWord) {
        PassWord = passWord;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getQq() {
        return Qq;
    }

    public void setQq(String qq) {
        Qq = qq;
    }

    public int getPoint() {
        return Point;
    }

    public void setPoint(int point) {
        Point = point;
    }

    public byte getIsOnline() {
        return IsOnline;
    }

    public void setIsOnline(byte isOnline) {
        IsOnline = isOnline;
    }

    public byte getIsLock() {
        return IsLock;
    }

    public void setIsLock(byte isLock) {
        IsLock = isLock;
    }
}
