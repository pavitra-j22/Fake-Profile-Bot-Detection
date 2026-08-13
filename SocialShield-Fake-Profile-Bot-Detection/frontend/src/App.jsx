import { useEffect, useMemo, useState } from "react";

const API = "https://fake-profile-bot-detection.onrender.com/api";

const blank = {
  username: "",
  displayName: "",
  followers: 1000,
  following: 500,
  posts: 20,
  accountAgeDays: 180,
  profilePicture: true,
  bioPresent: true,
  verified: false,
  averageLikes: 50,
  averageComments: 5,
  postsLast24h: 2,
  followersGainedLast7d: 20,
  suspiciousLogins: 0,
  deviceId: "DEVICE001",
  ipAddress: "192.168.1.10",
};

function Field({ label, ...props }) {
  return (
    <label className="field">
      <span>{label}</span>
      <input {...props} />
    </label>
  );
}

function Metric({ icon, label, value, tone }) {
  return (
    <div className={`metric ${tone || ""}`}>
      <div className="metric-icon">{icon}</div>
      <div>
        <span>{label}</span>
        <strong>{value}</strong>
      </div>
    </div>
  );
}

function RiskBadge({ status }) {
  return (
    <span className={`badge ${String(status || "").toLowerCase()}`}>
      {status || "UNKNOWN"}
    </span>
  );
}

export default function App() {
  const [form, setForm] = useState(blank);
  const [profiles, setProfiles] = useState([]);
  const [stats, setStats] = useState({
    total: 0,
    genuine: 0,
    suspicious: 0,
    bots: 0,
  });

  const [search, setSearch] = useState("");
  const [selected, setSelected] = useState(null);
  const [history, setHistory] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [dark, setDark] = useState(false);

  const load = async () => {
    try {
      const [profileResponse, statsResponse] = await Promise.all([
        fetch(`${API}/profiles?search=${encodeURIComponent(search)}`),
        fetch(`${API}/dashboard/stats`),
      ]);

      if (!profileResponse.ok || !statsResponse.ok) {
        throw new Error("Backend request failed");
      }

      const profileData = await profileResponse.json();
      const statsData = await statsResponse.json();

      setProfiles(profileData);
      setStats(statsData);
      setError("");
    } catch {
      setError(
        "Backend is not reachable. Make sure Spring Boot is running on port 8080."
      );
    }
  };

  useEffect(() => {
    load();
  }, [search]);

  const update = (e) => {
    const { name, value, type, checked } = e.target;

    setForm((old) => ({
      ...old,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const submit = async (e) => {
    e.preventDefault();

    setLoading(true);
    setError("");

    const numericFields = [
      "followers",
      "following",
      "posts",
      "accountAgeDays",
      "averageLikes",
      "averageComments",
      "postsLast24h",
      "followersGainedLast7d",
      "suspiciousLogins",
    ];

    const profile = { ...form };

    numericFields.forEach((key) => {
      profile[key] = Number(profile[key]);
    });

    try {
      const response = await fetch(`${API}/profiles`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(profile),
      });

      if (!response.ok) {
        throw new Error("Profile could not be analyzed.");
      }

      const result = await response.json();

      setSelected(result);

      const historyResponse = await fetch(
        `${API}/profiles/${result.id}/history`
      );

      if (historyResponse.ok) {
        setHistory(await historyResponse.json());
      }

      await load();

      setForm(blank);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const inspect = async (profile) => {
    setSelected(profile);

    try {
      const response = await fetch(
        `${API}/profiles/${profile.id}/history`
      );

      if (response.ok) {
        setHistory(await response.json());
      }
    } catch {
      setHistory([]);
    }
  };

  const remove = async (id) => {
    try {
      await fetch(`${API}/profiles/${id}`, {
        method: "DELETE",
      });

      setSelected(null);
      setHistory([]);
      await load();
    } catch {
      setError("Unable to remove profile.");
    }
  };

  const averageRisk = useMemo(() => {
    if (!profiles.length) return 0;

    return Math.round(
      profiles.reduce((sum, profile) => sum + profile.riskScore, 0) /
        profiles.length
    );
  }, [profiles]);

  const riskColor =
    selected?.riskScore >= 70
      ? "high"
      : selected?.riskScore >= 40
      ? "medium"
      : "low";

  return (
    <div className={dark ? "app dark" : "app"}>
      {/* NAVBAR */}
      <nav className="navbar">
        <div className="brand">
          <div className="brand-logo">🛡️</div>

          <div>
            <h1>SocialShield</h1>
            <span>AI SECURITY INTELLIGENCE</span>
          </div>
        </div>

        <div className="nav-actions">
          <div className="system-status">
            <span className="status-dot"></span>
            SYSTEM ONLINE
          </div>

          <button
            className="theme-button"
            onClick={() => setDark(!dark)}
          >
            {dark ? "☀️" : "🌙"}
          </button>
        </div>
      </nav>

      {/* HERO */}
      <section className="hero-section">
        <div>
          <span className="eyebrow">SOCIAL MEDIA THREAT DETECTION</span>

          <h2>
            Detect fake profiles.
            <br />
            <span>Protect real communities.</span>
          </h2>

          <p>
            Analyze behavioral signals and identify suspicious accounts,
            fake profiles, and automated bots using explainable risk
            intelligence.
          </p>
        </div>

        <div className="hero-shield">
          <div className="shield-ring">
            🛡️
          </div>
          <span>PROTECTED</span>
        </div>
      </section>

      {/* STATISTICS */}
      <section className="metrics">
        <Metric
          icon="👥"
          label="TOTAL PROFILES"
          value={stats.total}
        />

        <Metric
          icon="✓"
          label="GENUINE"
          value={stats.genuine}
          tone="green"
        />

        <Metric
          icon="⚠"
          label="SUSPICIOUS"
          value={stats.suspicious}
          tone="yellow"
        />

        <Metric
          icon="🤖"
          label="BOT ACCOUNTS"
          value={stats.bots}
          tone="red"
        />

        <Metric
          icon="◉"
          label="AVERAGE RISK"
          value={`${averageRisk}/100`}
        />
      </section>

      {/* MAIN */}
      <main className="main-grid">

        {/* ANALYSIS FORM */}
        <section className="card analysis-card">
          <div className="card-heading">
            <div>
              <span className="section-label">PROFILE ANALYZER</span>
              <h3>Run Security Analysis</h3>
              <p>
                Enter account information to calculate its risk score.
              </p>
            </div>

            <div className="engine">
              <span></span>
              RULE ENGINE
            </div>
          </div>

          <form onSubmit={submit}>

            <div className="form-section-title">
              👤 Account Information
            </div>

            <div className="two-columns">
              <Field
                label="Username"
                name="username"
                placeholder="example_user"
                value={form.username}
                onChange={update}
                required
              />

              <Field
                label="Display Name"
                name="displayName"
                placeholder="Example User"
                value={form.displayName}
                onChange={update}
              />
            </div>

            <div className="three-columns">
              <Field
                label="Followers"
                type="number"
                name="followers"
                value={form.followers}
                onChange={update}
              />

              <Field
                label="Following"
                type="number"
                name="following"
                value={form.following}
                onChange={update}
              />

              <Field
                label="Posts"
                type="number"
                name="posts"
                value={form.posts}
                onChange={update}
              />
            </div>

            <div className="three-columns">
              <Field
                label="Account Age (days)"
                type="number"
                name="accountAgeDays"
                value={form.accountAgeDays}
                onChange={update}
              />

              <Field
                label="Posts / 24 hours"
                type="number"
                name="postsLast24h"
                value={form.postsLast24h}
                onChange={update}
              />

              <Field
                label="New Followers / 7 days"
                type="number"
                name="followersGainedLast7d"
                value={form.followersGainedLast7d}
                onChange={update}
              />
            </div>

            <div className="form-section-title">
              📊 Behavioral Signals
            </div>

            <div className="three-columns">
              <Field
                label="Average Likes"
                type="number"
                name="averageLikes"
                value={form.averageLikes}
                onChange={update}
              />

              <Field
                label="Average Comments"
                type="number"
                name="averageComments"
                value={form.averageComments}
                onChange={update}
              />

              <Field
                label="Suspicious Logins"
                type="number"
                name="suspiciousLogins"
                value={form.suspiciousLogins}
                onChange={update}
              />
            </div>

            <div className="two-columns">
              <Field
                label="Device ID"
                name="deviceId"
                value={form.deviceId}
                onChange={update}
              />

              <Field
                label="IP Address"
                name="ipAddress"
                value={form.ipAddress}
                onChange={update}
              />
            </div>

            <div className="form-section-title">
              🔐 Account Verification
            </div>

            <div className="check-grid">

              <label className="check-card">
                <input
                  type="checkbox"
                  name="profilePicture"
                  checked={form.profilePicture}
                  onChange={update}
                />
                <div>
                  <strong>Profile Picture</strong>
                  <span>Account has a profile image</span>
                </div>
              </label>

              <label className="check-card">
                <input
                  type="checkbox"
                  name="bioPresent"
                  checked={form.bioPresent}
                  onChange={update}
                />
                <div>
                  <strong>Bio Present</strong>
                  <span>Account contains a biography</span>
                </div>
              </label>

              <label className="check-card">
                <input
                  type="checkbox"
                  name="verified"
                  checked={form.verified}
                  onChange={update}
                />
                <div>
                  <strong>Verified Account</strong>
                  <span>Platform verification enabled</span>
                </div>
              </label>

            </div>

            <button
              className="analyze-button"
              disabled={loading}
            >
              {loading ? (
                <>
                  <span className="spinner"></span>
                  ANALYZING PROFILE...
                </>
              ) : (
                <>
                  🛡️ RUN RISK ANALYSIS
                </>
              )}
            </button>

          </form>

          {error && (
            <div className="error-box">
              ⚠️ {error}
            </div>
          )}
        </section>

        {/* DETECTION QUEUE */}
        <section className="card queue-card">

          <div className="card-heading">
            <div>
              <span className="section-label">LIVE MONITORING</span>
              <h3>Detection Queue</h3>
            </div>

            <span className="queue-count">
              {profiles.length} ACCOUNTS
            </span>
          </div>

          <div className="search-box">
            🔍
            <input
              placeholder="Search username..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>

          <div className="queue">

            {!profiles.length && (
              <div className="empty-state">
                <div>🛡️</div>
                <strong>No profiles found</strong>
                <span>Run an analysis to create your first profile.</span>
              </div>
            )}

            {profiles
              .slice()
              .reverse()
              .map((profile) => (
                <button
                  className="profile-row"
                  key={profile.id}
                  onClick={() => inspect(profile)}
                >
                  <div className="profile-avatar">
                    {profile.username?.[0]?.toUpperCase() || "?"}
                  </div>

                  <div className="profile-info">
                    <strong>@{profile.username}</strong>

                    <span>
                      {Number(profile.followers).toLocaleString()} followers
                      {" · "}
                      {profile.postsLast24h} posts/day
                    </span>
                  </div>

                  <div className="profile-risk">
                    <strong>{profile.riskScore}</strong>
                    <RiskBadge status={profile.status} />
                  </div>
                </button>
              ))}
          </div>
        </section>
      </main>

      {/* RESULT */}
      {selected && (
        <section className="card result-card">

          <div className="result-header">
            <div>
              <span className="section-label">AI RISK DECISION</span>
              <h3>@{selected.username}</h3>
            </div>

            <RiskBadge status={selected.status} />
          </div>

          <div className="result-grid">

            <div className={`risk-score ${riskColor}`}>
              <span>RISK SCORE</span>

              <strong>
                {selected.riskScore}
                <small>/100</small>
              </strong>

              <div className="risk-bar">
                <div
                  style={{
                    width: `${Math.min(
                      selected.riskScore || 0,
                      100
                    )}%`,
                  }}
                ></div>
              </div>

              <p>
                {selected.riskScore >= 70
                  ? "High risk account"
                  : selected.riskScore >= 40
                  ? "Moderate risk account"
                  : "Low risk account"}
              </p>
            </div>

            <div className="result-stat">
              <span>CONFIDENCE</span>
              <strong>{selected.confidence}%</strong>
              <p>Detection confidence</p>
            </div>

            <div className="result-stat">
              <span>SEVERITY</span>
              <strong>{selected.severity}</strong>
              <p>Moderation priority</p>
            </div>

          </div>

          <div className="result-details">

            <div>
              <div className="sub-heading">
                <span>⚠️</span>
                Why was this account flagged?
              </div>

              <div className="reasons">
                {selected.reasons
                  ?.split(" • ")
                  .filter(Boolean)
                  .map((reason, index) => (
                    <div className="reason" key={index}>
                      <span>!</span>
                      {reason}
                    </div>
                  ))}

                {!selected.reasons && (
                  <div className="reason safe">
                    <span>✓</span>
                    No major risk signals detected.
                  </div>
                )}
              </div>
            </div>

            <div>
              <div className="sub-heading">
                <span>📜</span>
                Analysis History
              </div>

              <div className="history">

                {!history.length && (
                  <div className="history-empty">
                    No previous analysis records.
                  </div>
                )}

                {history.map((item) => (
                  <div className="history-row" key={item.id}>
                    <strong>{item.riskScore}/100</strong>

                    <RiskBadge status={item.status} />

                    <span>
                      {item.analyzedAt
                        ? new Date(
                            item.analyzedAt
                          ).toLocaleString()
                        : "Unknown"}
                    </span>
                  </div>
                ))}

              </div>
            </div>

          </div>

          <div className="result-footer">

            <div className="account-meta">
              <span>PROFILE ID</span>
              <strong>#{selected.id}</strong>
            </div>

            <button
              className="delete-button"
              onClick={() => remove(selected.id)}
            >
              🗑 Remove Profile
            </button>

          </div>
        </section>
      )}

      <footer>
        <span>🛡️ SocialShield</span>
        <span>AI-powered social security intelligence</span>
        <span>Backend API: localhost:8080</span>
      </footer>
    </div>
  );
}