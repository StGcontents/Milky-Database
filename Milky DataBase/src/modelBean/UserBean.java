package modelBean;

public class UserBean {
		
		private String userId; 
		private String nome;
		private String cognome;
		private String email;
		private String password;
		
		/**
		 * @return the userId
		 */
		public String getUserId() {
			return userId;
		}
		/**
		 * @param userId the userId to set
		 */
		public void setUserId(String userId) {
			this.userId = userId;
		}
		/**
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}
		/**
		 * @param password the password to set
		 */
		public void setPassword(String password) {
			this.password = password;
		}
		/**
		 * 
		 * @return nome
		 */
		public String getNome() {
			return nome;
		}
		/**
		 * 
		 * @param nome the nome to set
		 */
		public void setNome(String nome) {
			this.nome = nome;
		}
		/**
		 * 
		 * @return cognome
		 */
		public String getCognome() {
			return cognome;
		}
		/**
		 * 
		 * @param cognome the cognome to set
		 */
		public void setCognome(String cognome) {
			this.cognome = cognome;
		}
		/**
		 * 
		 * @return email
		 */
		public String getEmail() {
			return email;
		}
		/**
		 * 
		 * @param email the email to set 
		 */
		public void setEmail(String email) {
			this.email = email;
		}
	}
