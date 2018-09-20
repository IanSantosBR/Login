package iansantos.login.model;

public class User {
    private String name;
    private String lastName;
    private String email;
    private String cpf;
    private String password;

    public User(String name, String lastName, String email, String cpf, String password) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.cpf = cpf;
        this.password = password;
    }

    public User() {
    }

    public String getName() {
        return name = (name.substring(0, 1).toUpperCase() + name.substring(1)).trim();
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLastName() {
        return lastName = (lastName.substring(0, 1).toUpperCase() + lastName.substring(1)).trim();
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email = email.toLowerCase().trim();
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public String getPassword() {
        return password.trim();
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
