import { useState, useEffect } from 'react';

/*
 * PlayIT — React Prototype (Splash → Map)
 * ------------------------------------------------------------
 * A separate file from playit-mockup.html — same design system
 * (Lexend/Andika, mango/ube/guava/leaf/kalamansi, ink-outlined
 * "gummy" buttons), rebuilt as React components.
 *
 * UPDATED FOR AGENT HANDOFF (e.g. Gemini CLI + the nanobanana
 * image-generation extension). Two things changed to support that:
 *
 * 1. ASSET_MANIFEST / AUDIO_MANIFEST (below) are a machine-readable
 *    spec of every asset this UI actually needs, written as
 *    generation-ready prompts. A coding agent can `import` these
 *    and loop over them to drive asset creation directly, rather
 *    than inferring "what images does this need" from emoji/CSS.
 *    Generate tiko-reference FIRST, then generate every other Tiko
 *    pose FROM that reference image (not as independent prompts) —
 *    that's how you keep a mascot visually consistent across poses;
 *    generating each pose from scratch tends to drift in style.
 *
 * 2. Tiko now renders through <AssetImage>: it tries the real file
 *    at /assets/mascot/tiko-{pose}.png first, and only falls back
 *    to the hand-drawn CSS version if that file doesn't exist yet.
 *    So the UI works today with zero real assets, and upgrades
 *    itself automatically the moment generated files land at those
 *    paths — no code changes needed on either side of that handoff.
 *
 * REUSABLE ANIMATION PRIMITIVES:
 *   GummyButton   – the 3D press-able button (pill or circle) used
 *                   for every tap target: CTAs, map nodes, icons.
 *   PulseWrap     – wraps any element in a "look here" pulsing ring.
 *   Tiko          – the mascot; size + bob + pose props, image-first
 *                   with a CSS fallback (see above).
 *   ConfettiBurst – a short celebratory burst. Fire it by bumping a
 *                   counter prop (`trigger`) — no imperative DOM calls.
 *   StarRow       – staggered star reveal for celebration moments.
 *
 * Screens: SplashScreen (Duolingo-simple: mascot, wordmark, tagline,
 * one CTA) and MapScreen (tapping the current/completed node
 * completes it right there, switches Tiko to his "celebrate" pose
 * for a moment, and fires confetti — a compact preview of the full
 * lesson-complete moment without rebuilding Hear It/Say It/Find It
 * in React too).
 *
 * A few UI/UX choices below are deliberate, not defaults — sourced
 * from how children's-app research (NN/g, and general early-reader
 * accessibility guidance) describes the 5–7 age range specifically:
 *   - Touch targets 56–64px (well above the commonly-cited 44px
 *     adult minimum — young fingers need more room).
 *   - No red anywhere, including in error states (that constraint
 *     carried over from the HTML mockup, kept here too).
 *   - Wrong-answer feedback (tiko-encourage) is warm, never sad —
 *     punitive-feeling mistake states measurably discourage retrying.
 *   - Icon-only buttons (map nodes) get a real aria-label, not just
 *     a visual glyph — text labels alone don't serve screen readers,
 *     and a phonics app is exactly the kind of product where
 *     accessibility tends to get skipped and shouldn't be.
 *   - Audio is flagged as its own required asset category below,
 *     not an afterthought — for a phonics app, sound is arguably
 *     more load-bearing than any single illustration, and it's the
 *     easiest category to quietly drop when a team is focused on
 *     visuals.
 *
 * Portability note: this is web React + Tailwind (CSS keyframes via
 * a <style> tag). If the real app is React Native, component
 * boundaries and animation logic transfer directly, but styling
 * needs translating to StyleSheet + Animated/Reanimated — RN doesn't
 * run CSS, and <img onError> fallback would become Image's onError.
 */

const COLORS = {
  mango:  { base: '#FFC107', dark: '#E0A400', text: '#1F3A3D' },
  ube:    { base: '#8B5FBF', dark: '#6E3FA3', text: '#FFFFFF' },
  guava:  { base: '#FF6F91', dark: '#E14C71', text: '#FFFFFF' },
  leaf:   { base: '#48C774', dark: '#34A863', text: '#FFFFFF' },
  locked: { base: '#9DB0B2', dark: '#7C9295', text: '#FFFFFF' },
};

/* ============================================================
   ASSET MANIFEST — generation-ready specs, not just a wishlist.
   A coding agent (or a human running the nanobanana /generate and
   /icon commands by hand) can iterate this array directly. `path`
   is exactly where the UI below expects to find the file — drop
   the generated PNG there and it's live, no code changes.
   ============================================================ */

export const ASSET_MANIFEST = [
  {
    id: 'tiko-reference',
    type: 'character-reference',
    priority: 1,
    path: '/assets/mascot/tiko-reference.png',
    size: '1024x1024',
    usage: 'Not displayed directly. Generate this FIRST — every pose below should be generated FROM this image (image-to-image / reference input), not from an independent text prompt, so the character stays visually consistent.',
    prompt: "Character reference sheet for 'Tiko', a friendly Philippine tarsier mascot for a children's phonics app called PlayIT. Flat-vector illustration style, soft rounded shapes, thick clean outline. Warm tan-brown fur (#C9A06B), oversized round white eyes with dark pupils (a tarsier's signature trait), small rounded ears, tiny nose, simple friendly expression, sitting pose facing forward. Transparent background. No text, no shadow, no other characters, no scenery.",
  },
  {
    id: 'tiko-idle',
    type: 'character-pose',
    priority: 1,
    path: '/assets/mascot/tiko-idle.png',
    size: '512x512',
    usage: 'Default pose — splash screen, map screen, anywhere Tiko is just present.',
    prompt: 'Using the tiko-reference image as the character reference, generate the same character in a calm standing pose, one small arm slightly raised in a friendly half-wave, neutral happy expression. Transparent background, identical art style and colors to the reference.',
  },
  {
    id: 'tiko-celebrate',
    type: 'character-pose',
    priority: 1,
    path: '/assets/mascot/tiko-celebrate.png',
    size: '512x512',
    usage: 'Shown briefly whenever a letter/checkpoint completes (see mascotPose state in MapScreen below).',
    prompt: 'Using the tiko-reference image as the character reference, generate the same character mid-celebration: both small arms raised overhead, eyes scrunched into joyful happy arcs, a slight bounce in the pose. Transparent background, identical art style and colors to the reference.',
  },
  {
    id: 'tiko-encourage',
    type: 'character-pose',
    priority: 2,
    path: '/assets/mascot/tiko-encourage.png',
    size: '512x512',
    usage: "Wrong-answer / 'try again' moments. Keep this warm and supportive, never sad or disappointed — see the note on mistake-feedback above.",
    prompt: 'Using the tiko-reference image as the character reference, generate the same character leaning slightly forward, one hand gesturing forward as if cheering someone on, warm supportive expression. Not sad, not disappointed. Transparent background, identical art style and colors to the reference.',
  },
  {
    id: 'app-icon',
    type: 'app-icon',
    priority: 1,
    path: '/assets/icon/app-icon.png',
    size: '1024x1024 master — export 512/192/144/96/48 for the app stores',
    usage: 'OS home-screen icon.',
    prompt: 'App icon for a children\'s phonics app called PlayIT. Tiko the tarsier (see tiko-reference image) centered, sitting on a small rounded green hill, a simple sun in one corner, bold rounded shapes, vivid colors: mango yellow (#FFC107), ube purple (#8B5FBF), guava pink (#FF6F91) accents. Square canvas, keep the character within the center 80% of the frame so an OS mask can round the corners safely. No text.',
  },
];

/* Audio isn't something an image model can produce, so it can't take
   the same prompt-field shape — flagging it separately so it doesn't
   quietly fall off the asset backlog while everyone's looking at
   pictures. Needs either a TTS pass or (better, given the Tagalog
   phrases and the accuracy phonics demands) a real voice actor. */
export const AUDIO_MANIFEST = [
  { id: 'phoneme-a', usage: 'Hear It, letter A', script: 'Isolated /a/ sound as in "apple" — no carrier word, no extra emphasis.' },
  { id: 'phoneme-b', usage: 'Hear It, letter B', script: 'Isolated /b/ sound as in "ball".' },
  { id: 'word-success', usage: 'Short positive chime — Say It / Find It / Blend It correct answer.' },
  { id: 'word-tryagain', usage: 'Short, gentle, neutral tone on a wrong answer — explicitly NOT a buzzer or anything that reads as negative.' },
  { id: 'letter-complete-cheer', usage: 'Bigger celebratory sting for the star-reveal Complete screen.' },
  { id: 'vo-magaling', usage: '"Magaling!" (well done) — Filipino voice line, Say It/Find It success.' },
  { id: 'vo-tara-na', usage: '"Tara na!" (let\'s go) — Filipino voice line, map screen mascot bubble.' },
];

/* ============================================================
   REUSABLE ANIMATION PRIMITIVES
   ============================================================ */

function GummyButton({ children, onClick, color = 'mango', shape = 'pill', size = 'md', disabled = false, className = '', ariaLabel }) {
  const [pressed, setPressed] = useState(false);
  const c = COLORS[color] || COLORS.mango;
  const sizes = {
    sm: { pad: 'py-2 px-4', text: 'text-sm', dim: 56 },
    md: { pad: 'py-3.5 px-6', text: 'text-base', dim: 60 },
    lg: { pad: 'py-4 px-8', text: 'text-lg', dim: 64 },
  };
  const s = sizes[size] || sizes.md;
  const isCircle = shape === 'circle';

  const release = () => setPressed(false);

  return (
    <button
      aria-label={ariaLabel}
      disabled={disabled}
      onClick={onClick}
      onMouseDown={() => setPressed(true)}
      onMouseUp={release}
      onMouseLeave={release}
      onTouchStart={() => setPressed(true)}
      onTouchEnd={release}
      className={
        (isCircle ? 'rounded-full flex items-center justify-center text-2xl ' : `rounded-2xl ${s.pad} ${s.text} `) +
        'font-bold transition-transform select-none ' +
        (disabled ? 'opacity-40 cursor-not-allowed ' : 'cursor-pointer ') +
        className
      }
      style={{
        width: isCircle ? s.dim : undefined,
        height: isCircle ? s.dim : undefined,
        fontFamily: 'Lexend, sans-serif',
        color: c.text,
        background: `linear-gradient(160deg, ${c.base} 0%, ${c.dark} 100%)`,
        border: '3px solid #1F3A3D',
        boxShadow: pressed ? `0 1px 0 ${c.dark}` : `0 5px 0 ${c.dark}`,
        transform: pressed ? 'translateY(4px)' : 'translateY(0)',
      }}
    >
      {children}
    </button>
  );
}

// Tries the real asset first, falls back to a hand-drawn/emoji placeholder
// if it 404s. This is the seam Gemini CLI (or anyone) fills in later —
// drop a file at `src` and it replaces the fallback automatically.
function AssetImage({ src, alt = '', className = '', style = {}, fallback = null }) {
  const [failed, setFailed] = useState(false);
  if (!src || failed) return fallback;
  return (
    <img
      src={src}
      alt={alt}
      className={className}
      style={style}
      onError={() => setFailed(true)}
    />
  );
}

function PulseWrap({ children, color = 'mango' }) {
  const hex = (COLORS[color] || COLORS.mango).base;
  return (
    <div className="relative inline-flex items-center justify-center">
      <span className="absolute rounded-full pulse-ring" style={{ inset: -8, border: `3px solid ${hex}` }} />
      {children}
    </div>
  );
}

function Tiko({ size = 64, bob = false, pose = 'idle' }) {
  const scale = size / 64;
  const cssFallback = (
    <div style={{ transform: `scale(${scale})`, transformOrigin: 'center' }}>
      <div className={bob ? 'tiko-bob' : ''} style={{ position: 'relative', width: 64, height: 64 }}>
        <div style={{ position: 'absolute', top: 2, left: 1, width: 16, height: 16, background: '#C9A06B', borderRadius: '50%' }} />
        <div style={{ position: 'absolute', top: 2, right: 1, width: 16, height: 16, background: '#C9A06B', borderRadius: '50%' }} />
        <div style={{ position: 'absolute', inset: '9px 4px 2px 4px', background: '#C9A06B', borderRadius: '52% 52% 46% 46% / 58% 58% 42% 42%', boxShadow: 'inset 0 -8px 0 rgba(0,0,0,0.06)' }}>
          <div style={{ position: 'absolute', top: 20, left: 6, width: 23, height: 23, background: '#fff', borderRadius: '50%', boxShadow: '0 1px 3px rgba(0,0,0,0.18)' }}>
            <div style={{ position: 'absolute', top: 6, left: 6, width: 11, height: 11, background: '#1F3A3D', borderRadius: '50%' }} />
          </div>
          <div style={{ position: 'absolute', top: 20, right: 6, width: 23, height: 23, background: '#fff', borderRadius: '50%', boxShadow: '0 1px 3px rgba(0,0,0,0.18)' }}>
            <div style={{ position: 'absolute', top: 6, left: 6, width: 11, height: 11, background: '#1F3A3D', borderRadius: '50%' }} />
          </div>
          <div style={{ position: 'absolute', bottom: 11, left: '50%', transform: 'translateX(-50%)', width: 8, height: 6, background: '#8A6A42', borderRadius: '50%' }} />
        </div>
      </div>
    </div>
  );
  // NOTE: the CSS fallback above doesn't actually change shape per pose
  // (it's a placeholder, not a full illustration system) — only the real
  // generated assets from ASSET_MANIFEST differentiate idle/celebrate/
  // encourage visually. The bob animation still applies either way.
  return (
    <div style={{ width: size, height: size, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <AssetImage
        src={`/assets/mascot/tiko-${pose}.png`}
        alt="Tiko the tarsier"
        className={bob ? 'tiko-bob' : ''}
        style={{ width: '100%', height: '100%', objectFit: 'contain' }}
        fallback={cssFallback}
      />
    </div>
  );
}

function ConfettiBurst({ trigger }) {
  const [pieces, setPieces] = useState([]);
  useEffect(() => {
    if (!trigger) return;
    const colors = ['#8B5FBF', '#FFC107', '#FF6F91', '#48C774'];
    const next = Array.from({ length: 12 }).map((_, i) => ({
      id: trigger + '-' + i,
      dx: Math.random() * 160 - 80,
      dy: Math.random() * -120 - 40,
      rot: Math.random() * 360,
      color: colors[i % colors.length],
      left: 40 + Math.random() * 20,
    }));
    setPieces(next);
    const t = setTimeout(() => setPieces([]), 1200);
    return () => clearTimeout(t);
  }, [trigger]);

  return (
    <div className="pointer-events-none absolute inset-0 overflow-hidden">
      {pieces.map(p => (
        <div
          key={p.id}
          className="absolute w-2 h-2 rounded-sm confetti-piece"
          style={{ top: '40%', left: p.left + '%', background: p.color, '--dx': p.dx + 'px', '--dy': p.dy + 'px', '--rot': p.rot + 'deg' }}
        />
      ))}
    </div>
  );
}

function StarRow({ filled = 3, total = 3, size = 32, dimColor = 'rgba(255,255,255,0.3)' }) {
  return (
    <div className="flex" style={{ gap: Math.max(2, size * 0.15) }}>
      {Array.from({ length: total }).map((_, i) => (
        <span
          key={i}
          style={{
            fontSize: size,
            lineHeight: 1,
            display: 'inline-block',
            color: i < filled ? '#FFC107' : dimColor,
            animation: i < filled ? 'stardrop .55s cubic-bezier(.34,1.56,.64,1) backwards' : 'none',
            animationDelay: (i * 160) + 'ms',
          }}
        >★</span>
      ))}
    </div>
  );
}

/* ============================================================
   SCREENS
   ============================================================ */

function SplashScreen({ onStart }) {
  const [mounted, setMounted] = useState(false);
  useEffect(() => { const t = setTimeout(() => setMounted(true), 50); return () => clearTimeout(t); }, []);

  return (
    <div className="relative w-full h-full flex flex-col items-center justify-between overflow-hidden"
      style={{ background: 'linear-gradient(to top, #EBD9A6 0%, #EAF6FF 55%, #CFE9FF 100%)' }}>

      <div className="absolute bottom-0 left-0 right-0 h-24 flex items-end opacity-40 pointer-events-none">
        {[70, 100, 85, 110, 90].map((w, i) => (
          <div key={i} className="rounded-t-full flex-shrink-0" style={{ width: w, height: w * 0.6, marginLeft: i === 0 ? 0 : -16, background: '#C9A06B' }} />
        ))}
      </div>

      <div className={'flex-1 flex flex-col items-center justify-center gap-4 px-8 transition-all duration-700 ' + (mounted ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-4')}>
        <PulseWrap color="mango">
          <Tiko size={120} bob />
        </PulseWrap>
        <h1 className="font-black text-4xl tracking-tight" style={{ fontFamily: 'Lexend, sans-serif', color: '#1F3A3D' }}>PlayIT</h1>
        <p className="text-center font-semibold" style={{ fontFamily: 'Lexend, sans-serif', color: '#5C7679' }}>
          Learn to read, the fun way! 🎉
          <br />
          <span className="text-sm opacity-80">Matuto ng pagbasa, nang masaya!</span>
        </p>
      </div>

      <div className="w-full px-8 pb-10 flex flex-col items-center gap-3">
        <GummyButton color="mango" size="lg" onClick={onStart} className="w-full max-w-xs text-center">
          Tara, Simulan Na! 🚀
        </GummyButton>
        <p className="text-xs font-semibold" style={{ fontFamily: 'Lexend, sans-serif', color: '#5C7679' }}>Grade 1 · Marungko Phonics</p>
      </div>
    </div>
  );
}

function MapScreen() {
  const [nodes, setNodes] = useState([
    { id: 'A', x: 28, y: 84, status: 'complete', stars: 3, emoji: '🍎' },
    { id: 'B', x: 68, y: 66, status: 'current', stars: 0, emoji: '⚽' },
    { id: 'K', x: 24, y: 48, status: 'locked', stars: 0, emoji: '🪁' },
    { id: 'D', x: 66, y: 30, status: 'locked', stars: 0, emoji: '🐶' },
    { id: 'E', x: 30, y: 12, status: 'locked', stars: 0, emoji: '🥚' },
  ]);
  const [celebrate, setCelebrate] = useState(0);
  const [toast, setToast] = useState('');
  const [mascotPose, setMascotPose] = useState('idle');

  const totalStars = nodes.reduce((sum, n) => sum + (n.stars || 0), 0);
  const current = nodes.find(n => n.status === 'current');

  function celebrateNow() {
    setCelebrate(c => c + 1);
    setMascotPose('celebrate');
    setTimeout(() => setMascotPose('idle'), 1400);
  }

  // NOTE: tapping an unlocked/current node here completes it immediately —
  // a compact way to preview the celebration animation (confetti + star
  // pop-in + next-node unlock + Tiko switching to his "celebrate" pose)
  // without rebuilding the full Hear It/Say It/Find It lesson flow in
  // React as well.
  function tapNode(id) {
    const idx = nodes.findIndex(n => n.id === id);
    const n = nodes[idx];
    if (n.status === 'locked') {
      setToast('🔒 Finish the letter before this one!');
      setTimeout(() => setToast(''), 1800);
      return;
    }
    if (n.status === 'complete') {
      celebrateNow();
      return;
    }
    setNodes(prev => prev.map((node, i) => {
      if (i === idx) return { ...node, status: 'complete', stars: 3 };
      if (i === idx + 1 && node.status === 'locked') return { ...node, status: 'current' };
      return node;
    }));
    celebrateNow();
  }

  return (
    <div className="relative w-full h-full flex flex-col overflow-hidden" style={{ background: '#EAF6FF' }}>
      <div className="flex items-center justify-between px-4 py-3 z-20" style={{ background: 'rgba(255,255,255,0.85)', backdropFilter: 'blur(8px)', borderBottom: '1px solid rgba(31,58,61,0.06)' }}>
        <div className="flex items-center gap-2">
          <Tiko size={30} />
          <span className="font-extrabold" style={{ fontFamily: 'Lexend, sans-serif', color: '#1F3A3D' }}>Maya</span>
        </div>
        <div className="flex gap-2">
          <span className="text-sm font-extrabold px-2.5 py-1 rounded-full" style={{ background: '#FFC107', color: '#8A6A42', fontFamily: 'Lexend, sans-serif' }}>🔥 5</span>
          <span className="text-sm font-extrabold px-2.5 py-1 rounded-full" style={{ background: '#EEE3F8', color: '#6E3FA3', fontFamily: 'Lexend, sans-serif' }}>⭐ {totalStars}</span>
        </div>
      </div>

      <div className="relative flex-1 overflow-hidden" style={{ background: 'linear-gradient(to top, #EBD9A6 0%, #EAF6FF 48%, #CFE9FF 100%)' }}>
        <div className="absolute bottom-0 left-0 right-0 h-20 flex items-end opacity-90 pointer-events-none">
          {[95, 110, 85, 120, 90, 100].map((w, i) => (
            <div key={i} className="rounded-t-full flex-shrink-0" style={{ width: w, height: w * 0.62, marginLeft: i === 0 ? 0 : -16, background: i % 2 === 0 ? '#C9A06B' : '#8A6A42' }} />
          ))}
        </div>
        <span className="absolute pointer-events-none" style={{ right: '4%', top: '6%', fontSize: 40 }}>🌴</span>
        <span className="absolute pointer-events-none" style={{ left: '3%', top: '30%', fontSize: 24, opacity: .7 }}>🌴</span>
        <span className="absolute pointer-events-none" style={{ left: '5%', top: '54%', fontSize: 26 }}>🛖</span>

        <svg className="absolute inset-0 w-full h-full" viewBox="0 0 100 100" preserveAspectRatio="none">
          <path d="M28,84 C28,76 68,76 68,66 C68,57 24,57 24,48 C24,39 66,39 66,30 C66,21 30,21 30,12"
            fill="none" stroke="#B98A4F" strokeWidth="2.4" strokeLinecap="round" strokeDasharray="4 3.4" />
        </svg>

        {nodes.map(n => {
          const color = n.status === 'complete' ? 'leaf' : n.status === 'locked' ? 'locked' : 'mango';
          const label = n.status === 'locked'
            ? `Letter ${n.id}, locked`
            : n.status === 'complete'
              ? `Letter ${n.id}, completed, ${n.stars} stars`
              : `Letter ${n.id}, current lesson`;
          const badge = (
            <GummyButton shape="circle" color={color} onClick={() => tapNode(n.id)} ariaLabel={label}>
              {n.status === 'locked' ? '🔒' : n.emoji}
            </GummyButton>
          );
          return (
            <div key={n.id} className="absolute flex flex-col items-center gap-1" style={{ left: n.x + '%', top: n.y + '%', transform: 'translate(-50%,-50%)' }}>
              {n.status === 'current' ? <PulseWrap color="mango">{badge}</PulseWrap> : badge}
              <span className="text-[10px] font-bold px-1.5 rounded" style={{ fontFamily: 'Andika, serif', color: '#5C7679', background: 'rgba(255,255,255,0.75)' }}>{n.id}</span>
              {n.status === 'complete' && <StarRow filled={n.stars} total={3} size={10} dimColor="#9DB0B2" />}
            </div>
          );
        })}

        {current && (
          <div className="absolute flex flex-col items-center" style={{ left: (current.x + 11) + '%', top: (current.y + 6) + '%' }}>
            <div className="mb-1 px-2.5 py-1 rounded-xl text-xs font-extrabold whitespace-nowrap" style={{ background: '#fff', border: '2px solid #1F3A3D', color: '#1F3A3D', fontFamily: 'Lexend, sans-serif' }}>Tara na!</div>
            <Tiko size={84} bob pose={mascotPose} />
          </div>
        )}

        <ConfettiBurst trigger={celebrate} />

        {toast && (
          <div className="absolute left-1/2 bottom-5 -translate-x-1/2 px-4 py-2.5 rounded-full text-white text-sm font-bold whitespace-nowrap" style={{ background: '#1F3A3D', fontFamily: 'Lexend, sans-serif' }}>{toast}</div>
        )}
      </div>
    </div>
  );
}

/* ============================================================
   ROOT
   ============================================================ */

export default function PlayItReactPrototype() {
  const [screen, setScreen] = useState('splash');

  useEffect(() => {
    const link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = 'https://fonts.googleapis.com/css2?family=Lexend:wght@400;500;600;700;800&family=Andika:wght@400;700&display=swap';
    document.head.appendChild(link);
    return () => { document.head.removeChild(link); };
  }, []);

  return (
    <div className="min-h-screen w-full flex flex-col items-center justify-center gap-4 p-6" style={{ background: '#EFEBE0' }}>
      <style>{`
        @keyframes bob{0%,100%{transform:translateY(0);}50%{transform:translateY(-6px);}}
        @keyframes ringpulse{0%{transform:scale(0.85);opacity:.9;}100%{transform:scale(1.35);opacity:0;}}
        @keyframes confetti{
          0%{transform:translate(-50%,-50%) rotate(0deg);opacity:1;}
          100%{transform:translate(calc(-50% + var(--dx)), calc(-50% + var(--dy))) rotate(var(--rot));opacity:0;}
        }
        @keyframes stardrop{0%{transform:translateY(-40px) scale(0.4);opacity:0;}100%{transform:translateY(0) scale(1);opacity:1;}}
        .tiko-bob{animation:bob 2.6s ease-in-out infinite;}
        .pulse-ring{animation:ringpulse 1.8s ease-out infinite;}
        .confetti-piece{animation:confetti 1.1s ease-out forwards;}
        button:focus-visible{outline:3px solid #6E3FA3; outline-offset:2px;}
        @media (prefers-reduced-motion: reduce){
          .tiko-bob, .pulse-ring, .confetti-piece{animation:none !important;}
        }
      `}</style>

      <div className="relative overflow-hidden" style={{ width: 390, maxWidth: '92vw', height: 780, maxHeight: '82vh', borderRadius: 44, border: '10px solid #16211f', boxShadow: '0 30px 60px -20px rgba(20,32,31,0.4)' }}>
        <div className="absolute top-0 left-1/2 -translate-x-1/2 z-50" style={{ width: 112, height: 20, background: '#0d1615', borderRadius: '0 0 16px 16px' }} />
        {screen === 'splash' ? <SplashScreen onStart={() => setScreen('map')} /> : <MapScreen />}
      </div>

      <button onClick={() => setScreen('splash')} className="text-xs font-bold" style={{ fontFamily: 'Lexend, sans-serif', color: '#5C7679' }}>
        ↺ Restart demo
      </button>
    </div>
  );
}
