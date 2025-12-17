# ---------------------------------------------------------------
# 1. ENABLE PER-LISTENER SETTINGS (Crucial Step)
# ---------------------------------------------------------------
per_listener_settings true
pid_file /var/run/mosquitto/mosquitto.pid
persistence true
persistence_location /data/
log_dest stdout
log_timestamp_format %Y-%m-%d %H:%M:%S

{{ if .debug }}
log_type all
{{ else }}
log_type error
log_type warning
log_type notice
log_type information
{{ end }}

# ---------------------------------------------------------------
# 2. LISTENER 1883: ANONYMOUS / OPEN (For your ESP Devices)
# ---------------------------------------------------------------
listener 1883
protocol mqtt
allow_anonymous true
# Note: NO auth_plugin lines here. This makes it a standard, open broker.

# ---------------------------------------------------------------
# 3. LISTENER 1884: SECURE / HOME ASSISTANT
#    (We moved the plugin logic here)
# ---------------------------------------------------------------
listener 1884
protocol mqtt
allow_anonymous true

# --- Auth Plugin Configuration (Moved inside Listener 1884) ---
auth_opt_backends files,http
auth_opt_hasher pbkdf2
auth_opt_cache true
auth_opt_auth_cache_seconds 300
auth_opt_auth_jitter_seconds 30
auth_opt_acl_cache_seconds 300
auth_opt_acl_jitter_seconds 30
auth_opt_log_level {{ if .debug }}debug{{ else }}error{{ end }}

# HTTP backend
auth_opt_files_password_path /etc/mosquitto/pw
auth_opt_files_acl_path /etc/mosquitto/acl
auth_opt_http_host 127.0.0.1
auth_opt_http_port 80
auth_opt_http_getuser_uri /authentication
auth_opt_http_superuser_uri /superuser
auth_opt_http_aclcheck_uri /acl
# ---------------------------------------------------------------

{{ if .customize }}
include_dir /share/{{ .customize_folder }}
{{ end }}